import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

// Custom metrics
const summaryResponseTime = new Trend('summary_response_time');
const createResponseTime = new Trend('create_response_time');
const listResponseTime = new Trend('list_response_time');
const errorRate = new Rate('errors');

// Get target from environment variable
const TARGET = __ENV.TARGET || 'http://localhost:8080';
const STACK_NAME = __ENV.STACK_NAME || 'unknown';

export const options = {
  stages: [
    { duration: '20s', target: 10 },   // Warm up
    { duration: '30s', target: 50 },   // Ramp up
    { duration: '40s', target: 50 },   // Sustained load
    { duration: '10s', target: 0 },    // Cool down
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95% of requests under 1s
    errors: ['rate<0.05'],             // Error rate under 5%
  },
};

export default function () {
  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  // 1. GET Summary
  let res = http.get(`${TARGET}/transactions/summary`, params);
  summaryResponseTime.add(res.timings.duration);
  errorRate.add(res.status !== 200);
  check(res, { 'summary status 200': (r) => r.status === 200 });
  sleep(0.5);

  // 2. GET Expenses with filters
  const today = new Date().toISOString().split('T')[0];
  res = http.get(
    `${TARGET}/transactions/expenses?startDate=2024-01-01&endDate=${today}`,
    params
  );
  listResponseTime.add(res.timings.duration);
  errorRate.add(res.status !== 200);
  check(res, { 'expenses status 200': (r) => r.status === 200 });
  sleep(0.5);

  // 3. POST Create Transaction
  const payload = JSON.stringify({
    title: 'Benchmark Transaction',
    amount: (Math.random() * 100 + 1).toFixed(2),
    type: Math.random() > 0.5 ? 'income' : 'expense',
    category: 'test',
  });

  res = http.post(`${TARGET}/transactions`, payload, params);
  createResponseTime.add(res.timings.duration);
  errorRate.add(res.status !== 201 && res.status !== 200);
  check(res, {
    'create status 2xx': (r) => r.status === 201 || r.status === 200,
  });
  sleep(1);
}

export function handleSummary(data) {
  const filename = `benchmarks/results/${STACK_NAME}-${new Date().toISOString().split('T')[0]}.json`;
  return {
    [filename]: JSON.stringify(data, null, 2),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}