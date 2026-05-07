# Performance Benchmarks

## Methodology

Both stacks are tested on the same hardware, same PostgreSQL instance, 
with identical load patterns.

### Test Environment
- Hardware: [Your specs - CPU, RAM]
- Database: PostgreSQL 18, local instance
- Test Tool: k6

### Test Scenarios
1. Warm-up: 10 users for 20 seconds
2. Ramp-up: 50 users for 30 seconds  
3. Sustained load: 50 users for 40 seconds
4. Cool-down: 10 seconds

Each scenario tests three endpoints:
- GET /transactions/summary (simple aggregation)
- GET /transactions/expenses (filtered query)
- POST /transactions (write operation)

