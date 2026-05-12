import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.2/index.js';

const summaryResponseTime = new Trend('summary_response_time');
const createResponseTime = new Trend('create_response_time');
const listResponseTime = new Trend('list_response_time');
const errorRate = new Rate('errors');

const TARGET = __ENV.TARGET || 'http://localhost:8081';
const STACK_NAME = __ENV.STACK_NAME || 'springboot';

export const options = {
  stages: [
    { duration: '20s', target: 10 },
    { duration: '30s', target: 50 },
    { duration: '40s', target: 50 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'],
    errors: ['rate<0.05'],
  },
};

export default function () {
  const params = {
    headers: { 'Content-Type': 'application/json' },
  };
  const today = new Date().toISOString().split('T')[0];

  // 1. GET Summary (no trailing slash)
  let res = http.get(`${TARGET}/transactions/summary`, params);
  summaryResponseTime.add(res.timings.duration);
  errorRate.add(res.status !== 200);
  check(res, { 'summary status 200': (r) => r.status === 200 });
  sleep(0.5);

  // 2. GET Expenses (no trailing slash)
  res = http.get(`${TARGET}/transactions/summary/expenses?startDate=2024-01-01&endDate=${today}`, params);
  listResponseTime.add(res.timings.duration);
  errorRate.add(res.status !== 200);
  check(res, { 'expenses status 200': (r) => r.status === 200 });
  sleep(0.5);

  // 3. POST Create Transaction (no trailing slash)
  const payload = JSON.stringify({
    title: 'Benchmark Transaction',
    amount: parseFloat((Math.random() * 100 + 1).toFixed(2)),
    type: Math.random() > 0.5 ? 'income' : 'expense',
    category: 'test',
    date: today,
  });

  res = http.post(`${TARGET}/transactions`, payload, params);
  createResponseTime.add(res.timings.duration);
  errorRate.add(res.status !== 201 && res.status !== 200);
  check(res, { 'create status 2xx': (r) => r.status === 201 || r.status === 200 });
  sleep(1);
}

export function handleSummary(data) {
  const filename = `benchmarks/results/${STACK_NAME}-${new Date().toISOString().split('T')[0]}.json`;
  return {
    [filename]: JSON.stringify(data, null, 2),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}