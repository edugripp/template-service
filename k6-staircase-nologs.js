import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';
import exec from 'k6/execution';

// Custom metric to summarize the total successful requests explicitly in the terminal output
export const successfulRequests = new Counter('successful_requests_valid');

export const options = {
    scenarios: {
        constant_load: {
            executor: 'constant-arrival-rate',
            rate: 1000,
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 1000,
            maxVUs: 10000,
        },
    },
    thresholds: {
        http_req_duration: [
            {
                threshold: 'p(99)<1000',
                abortOnFail: true,
                delayAbortEval: '5s'
            }
        ],
    },
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

const BASE_URL = 'http://localhost:9090';

export function setup() {

    const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
        username: 'admin',
        password: 'admin'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    let token = '';
    try {
        if (loginRes.status === 200) {
            token = loginRes.json('token');
        }
    } catch (e) { }

    return { token: token };
}

export default function (data) {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.token}`,
        },
    };

    const actionType = Math.random(); // 0.0 to 1.0
    let res;

    if (actionType < 0.6) {
        // 60% chance: GET /template (Paginated List)
        const randomPage = Math.floor(Math.random() * 10) + 1; // 1 to 10
        const randomPageSize = Math.floor(Math.random() * 41) + 10; // 10 to 50
        res = http.get(`${BASE_URL}/template?page=${randomPage}&pageSize=${randomPageSize}`, params);

        const success = check(res, { 'GET List status 200 or 404': (r) => r.status === 200 || r.status === 404 });
        if (success) successfulRequests.add(1);

    } else if (actionType < 0.9) {
        // 30% chance: GET /template/{id} (Single Item ID)
        const randomId = Math.floor(Math.random() * 500) + 1; // ID 1 to 500
        res = http.get(`${BASE_URL}/template/${randomId}`, params);

        const success = check(res, { 'GET ID status 200 or 404': (r) => r.status === 200 || r.status === 404 });
        if (success) successfulRequests.add(1);

    } else {
        // 10% chance: POST /template (Create)
        const payload = JSON.stringify({
            description: `K6 Load Test Insert - VU: ${exec.vu.idInTest} - ITER: ${exec.vu.iterationInInstance} - TS: ${Date.now()}`
        });
        res = http.post(`${BASE_URL}/template/`, payload, params);

        const success = check(res, { 'POST Insert status 201': (r) => r.status === 201 });
        if (success) {
            successfulRequests.add(1);
        } else {
            console.error(`POST Failed! Status: ${res.status} Body: ${res.body}`);
        }
    }
}

