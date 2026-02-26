import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 20 },   // 20
        { duration: '30s', target: 40 },   // 40
        { duration: '30s', target: 60 },   // 60  
        { duration: '30s', target: 80 },   // 80
        { duration: '30s', target: 100 },  // 100
        { duration: '30s', target: 120 },  // 120
        { duration: '30s', target: 140 },  // 140
        { duration: '30s', target: 160 },  // 160
        { duration: '30s', target: 180 },  // 180
        { duration: '30s', target: 200 },  // 200
        { duration: '30s', target: 0 },    // Cooldown
    ],
    thresholds: {
        // Nós VAMOS cruzar isso e falhar de propósito. Queremos ver a degradação!
        http_req_duration: ['p(95)<2000'],
    },
};

const BASE_URL = 'http://localhost:9090/template';

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic YWRtaW46YWRtaW4=',
        },
    };

    group('1. Criar Template (POST)', function () {
        const payload = JSON.stringify({
            description: `Carga limite - VU ${__VU} Iteração ${__ITER}`,
        });

        let postRes = http.post(`${BASE_URL}/`, payload, params);

        let createdId = 1; // Fallback
        if (postRes.status === 201 || postRes.status === 200) {
            try { createdId = postRes.json('id'); } catch (e) { }
        }

        console.log(`[VUs Ativos: ${__VU}] Inserção POST levou: ${postRes.timings.duration} ms`);

        group('2. Buscar Template por ID (GET)', function () {
            http.get(`${BASE_URL}/${createdId}`, params);
        });

        group('3. Atualizar Template (PUT)', function () {
            const putPayload = JSON.stringify({ description: `Atualização - VU ${__VU}` });
            http.put(`${BASE_URL}/${createdId}`, putPayload, params);
        });
    });

    group('4. Listar Templates (GET List)', function () {
        http.get(`${BASE_URL}?page=1&pageSize=10`, params);
    });

    sleep(1);
}
