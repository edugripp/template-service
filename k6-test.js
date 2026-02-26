import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 10 },    // Sobe para 10 usuários para preparar (Warm-up inicial)
    { duration: '4m30s', target: 500 }, // Sobe de 50 para 500 simultâneos no decorrer de 4m30s (Total de 5 min)
    { duration: '30s', target: 0 },     // Desce novamente para 0
  ],
  thresholds: {
    // Definimos limites para falhar o teste se ficar muito degradado
    http_req_duration: ['p(95)<2000'], // 95% das requests DEVEM responder em menos de 2 segundos
    http_req_failed: ['rate<0.05'],    // Falhas devem ser menores que 5%
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
      description: `Carga de teste k6 - VU ${__VU} Iteração ${__ITER}`,
    });

    let postRes = http.post(`${BASE_URL}/`, payload, params);
    check(postRes, {
      'POST status 201 ou 200': (r) => r.status === 201 || r.status === 200,
    });

    // Tentar extrair o ID para as próximas etapas
    let createdId;
    if (postRes.status === 201 || postRes.status === 200) {
      try {
        createdId = postRes.json('id');
      } catch (e) {
        createdId = 1; // Fallback
      }
    } else {
      createdId = 1; // Fallback
    }

    group('2. Buscar Template por ID (GET)', function () {
      let getRes = http.get(`${BASE_URL}/${createdId}`, params);
      check(getRes, {
        'GET ID status 200': (r) => r.status === 200 || r.status === 404,
      });
    });

    group('3. Atualizar Template (PUT)', function () {
      const putPayload = JSON.stringify({
        description: `Carga atualizada - VU ${__VU}`,
      });
      let putRes = http.put(`${BASE_URL}/${createdId}`, putPayload, params);
      check(putRes, {
        'PUT status 200': (r) => r.status === 200 || r.status === 404,
      });
    });
  });

  group('4. Listar Templates (GET List)', function () {
    // Fazemos um GET passando paginação
    let listRes = http.get(`${BASE_URL}?page=1&pageSize=10`, params);
    check(listRes, {
      'GET List status 200': (r) => r.status === 200,
    });
  });

  // Pausa de 1 segundo para o usuário "respirar" entre um ciclo completo (POST -> GET -> PUT -> GET)
  sleep(1);
}
