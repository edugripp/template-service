import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        staircase: {
            executor: 'ramping-arrival-rate',
            startRate: 1,        // Começamos o teste MUITO leve: 1 requisição por segundo
            timeUnit: '1s',
            preAllocatedVUs: 10,  // Poucos VUs base alocados inicialmente
            maxVUs: 1000,
            stages: [
                { duration: '10s', target: 5 },   // Sobe para 5 req/s
                { duration: '10s', target: 10 },  // 10 req/s
                { duration: '10s', target: 15 },  // 15 req/s
                { duration: '10s', target: 20 },  // 20 req/s
                { duration: '10s', target: 25 },  // 25 req/s
                { duration: '10s', target: 30 },  // 30 req/s
                { duration: '10s', target: 35 },  // 35 req/s
                { duration: '10s', target: 40 },  // 40 req/s
                { duration: '10s', target: 45 },  // 45 req/s
                { duration: '10s', target: 50 },  // 50 req/s
                { duration: '10s', target: 60 },
                { duration: '10s', target: 70 },
                { duration: '10s', target: 80 },
                { duration: '10s', target: 90 },
                { duration: '10s', target: 100 },
            ],
        },
    },
    thresholds: {
        // O segredo está aqui: assim que 99% das requisições baterem mais de 1000ms (1 segundo),
        // o K6 aborta o teste automaticamente após avaliar por 5 segundos seguidos.
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

const BASE_URL = 'http://localhost:9090/template';

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic YWRtaW46YWRtaW4=',
        },
    };

    // Fazemos apenas GET para medir a performance pura de I/O do banco via Virtual Threads
    let listRes = http.get(`${BASE_URL}?page=1&pageSize=10`, params);

    check(listRes, {
        'GET List status 200': (r) => r.status === 200,
    });

    // Aguarda 100ms. Cada VU fará cerca de 10 chamadas por segundo.
    //sleep(0.1);
}
