import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';

// Custom metric to summarize the total successful requests explicitly in the terminal output
export const successfulRequests = new Counter('successful_requests_200_ok');

export const options = {
    scenarios: {
        staircase: {
            executor: 'ramping-arrival-rate',
            startRate: 1,
            timeUnit: '1s',
            preAllocatedVUs: 10,
            maxVUs: 3000,
            stages: [
                { duration: '10s', target: 50 },
                { duration: '10s', target: 100 },
                { duration: '10s', target: 200 },
                { duration: '10s', target: 300 },
                { duration: '10s', target: 400 },
                { duration: '10s', target: 500 },
                { duration: '10s', target: 600 },
                { duration: '10s', target: 800 },
                { duration: '10s', target: 1000 },
                { duration: '10s', target: 1300 },
                { duration: '10s', target: 1600 },
                { duration: '10s', target: 2000 },
            ],
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
    console.log("-----------------------------------------");
    console.log("🚀 K6 Load Test Starting: Aiming for 2000 RPS");
    console.log("-----------------------------------------");

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
            console.log("✅ Successfully retrieved JWT Token!");
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

    const randomPage = Math.floor(Math.random() * 10) + 1; // 1 to 10
    const randomPageSize = Math.floor(Math.random() * 41) + 10; // 10 to 50

    let listRes = http.get(`${BASE_URL}/template?page=${randomPage}&pageSize=${randomPageSize}`, params);

    const success = check(listRes, {
        'GET List status 200': (r) => r.status === 200,
    });

    if (success) {
        successfulRequests.add(1);
    }
}

