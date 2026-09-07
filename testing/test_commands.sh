#!/bin/bash
# TriNetra AI - 15 Synthetic Claims Automated API Curl Test Suite

HOST="http://localhost:8080/api/claims/verdict"

echo "=== STARTING TRINETRA SYNTHETIC DATA VALIDATION ==="

# 1. CLAIM-SYN-001
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-001","merchantId":"MCH-ZARA","customerId":"CUST-001","orderId":"ORD-SYN-1","outboundSku":"NK-RN-FLY-12","outboundWeightGrams":650.0,"returnSku":"NK-RN-FLY-12","returnWeightGrams":650.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T12:00:00","returnTimestamp":"2026-09-03T14:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-001","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-001","source":"COURIER_VOICE","payloadJson":"{}","createdAt":"2026-09-03T14:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 2. CLAIM-SYN-002
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-002","merchantId":"MCH-ZARA","customerId":"CUST-002","orderId":"ORD-SYN-2","outboundSku":"NK-RN-FLY-12","outboundWeightGrams":650.0,"returnSku":"NK-RN-FLY-12","returnWeightGrams":637.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T12:00:00","returnTimestamp":"2026-09-03T14:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-002","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-002","source":"COURIER_VOICE","payloadJson":"{}","createdAt":"2026-09-03T14:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 3. CLAIM-SYN-003
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-003","merchantId":"MCH-LEVI","customerId":"CUST-003","orderId":"ORD-SYN-3","outboundSku":"LEVI-JEANS-501","outboundWeightGrams":450.0,"returnSku":"LEVI-JEANS-501","returnWeightGrams":450.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-05T14:32:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-003","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-003","source":"COURIER_VOICE","payloadJson":"{}","createdAt":"2026-09-05T14:32:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 4. CLAIM-SYN-004
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-004","merchantId":"MCH-APPLE","customerId":"CUST-004","orderId":"ORD-SYN-4","outboundSku":"APPLE-IPHONE-15","outboundWeightGrams":642.0,"returnSku":"APPLE-IPHONE-15","returnWeightGrams":410.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-04T12:15:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-004","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-004","source":"CARRIER_HUB","payloadJson":"{}","createdAt":"2026-09-04T12:15:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 5. CLAIM-SYN-005
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-005","merchantId":"MCH-SONY","customerId":"CUST-005","orderId":"ORD-SYN-5","outboundSku":"SNY-WH1000-XM5","outboundWeightGrams":800.0,"returnSku":"SNY-WH1000-XM5","returnWeightGrams":200.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-04T12:15:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-005","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-005","source":"CARRIER_HUB","payloadJson":"{}","createdAt":"2026-09-04T12:15:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 6. CLAIM-SYN-006
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-006","merchantId":"MCH-APPLE","customerId":"CUST-006","orderId":"ORD-SYN-6","outboundSku":"APPLE-IPHONE-15","outboundWeightGrams":642.0,"returnSku":"APPLE-IPHONE-15","returnWeightGrams":605.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-04T12:15:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-006","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-006","source":"CARRIER_HUB","payloadJson":"{}","createdAt":"2026-09-04T12:15:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 7. CLAIM-SYN-007
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-007","merchantId":"MCH-SAMSUNG","customerId":"CUST-007","orderId":"ORD-SYN-7","outboundSku":"SAM-WATCH-6","outboundWeightGrams":500.0,"returnSku":"SAM-WATCH-6","returnWeightGrams":485.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-04T12:15:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-007","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-007","source":"COURIER_VOICE","payloadJson":"{}","createdAt":"2026-09-04T12:15:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 8. CLAIM-SYN-008
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-008","merchantId":"MCH-NIKE","customerId":"CUST-008","orderId":"ORD-SYN-8","outboundSku":"NIKE-123","outboundWeightGrams":380.0,"returnSku":"NIKE-FAKE-456","returnWeightGrams":380.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-05T09:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-008","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-008","source":"CONSUMER_VIDEO","payloadJson":"{}","createdAt":"2026-09-05T09:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 9. CLAIM-SYN-009
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-009","merchantId":"MCH-ADIDAS","customerId":"CUST-009","orderId":"ORD-SYN-9","outboundSku":"ADIDAS-ULTRABOOST","outboundWeightGrams":380.0,"returnSku":"ADIDAS-ULTRABOOST-FAKE","returnWeightGrams":380.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-05T09:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-009","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-009","source":"CONSUMER_VIDEO","payloadJson":"{}","createdAt":"2026-09-05T09:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 10. CLAIM-SYN-010
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-010","merchantId":"MCH-SAMSUNG","customerId":"CUST-010","orderId":"ORD-SYN-10","outboundSku":"SAM-GAL-S24","outboundWeightGrams":380.0,"returnSku":"SAM-GAL-S24","returnWeightGrams":380.0,"outboundTimestamp":"2026-09-01T08:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-02T09:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-010","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T08:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-010","source":"COURIER_VOICE","payloadJson":"{}","createdAt":"2026-09-02T09:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 11. CLAIM-SYN-011
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-011","merchantId":"MCH-SAMSUNG","customerId":"CUST-011","orderId":"ORD-SYN-11","outboundSku":"SAM-GAL-S24","outboundWeightGrams":380.0,"returnSku":"SAM-GAL-S24","returnWeightGrams":380.0,"outboundTimestamp":"2026-09-01T08:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-01T07:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-011","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T08:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-011","source":"COURIER_VOICE","payloadJson":"{}","createdAt":"2026-09-01T07:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 12. CLAIM-SYN-012
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-012","merchantId":"MCH-SONY","customerId":"CUST-012","orderId":"ORD-SYN-12","outboundSku":"SNY-WH1000-XM5","outboundWeightGrams":500.0,"returnSku":"SNY-WH1000-XM5","returnWeightGrams":500.0,"outboundTimestamp":"2026-09-01T12:00:00","deliveryTimestamp":"2026-09-02T14:00:00","returnTimestamp":"2026-09-04T16:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-012","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T12:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 13. CLAIM-SYN-013
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-013","merchantId":"MCH-SONY","customerId":"CUST-013","orderId":"ORD-SYN-13","outboundSku":"SNY-WH1000-XM5","outboundWeightGrams":500.0,"returnSku":"SNY-WH1000-XM5","returnWeightGrams":500.0,"outboundTimestamp":"2026-09-01T12:00:00","deliveryTimestamp":"2026-09-02T14:00:00","returnTimestamp":"2026-09-04T16:00:00","status":"PENDING"},
  "evidence": []
}' | grep -o '"conflictType":"[^"]*"'

# 14. CLAIM-SYN-014
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-014","merchantId":"MCH-TEST","customerId":"CUST-014","orderId":"ORD-SYN-14","outboundSku":"TEST-SKU-01","outboundWeightGrams":0.0,"returnSku":"TEST-SKU-01","returnWeightGrams":100.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-05T09:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-014","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-014","source":"COURIER_VOICE","payloadJson":"{}","createdAt":"2026-09-05T09:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

# 15. CLAIM-SYN-015
curl -s -X POST $HOST -H "Content-Type: application/json" -d '{
  "claim": {"claimId":"CLAIM-SYN-015","merchantId":"MCH-TEST","customerId":"CUST-015","orderId":"ORD-SYN-15","outboundSku":"TEST-SKU-02","outboundWeightGrams":500.0,"returnSku":"TEST-SKU-02","returnWeightGrams":550.0,"outboundTimestamp":"2026-09-01T10:00:00","deliveryTimestamp":"2026-09-02T18:00:00","returnTimestamp":"2026-09-05T09:00:00","status":"PENDING"},
  "evidence": [{"evidenceId":"e1","claimId":"CLAIM-SYN-015","source":"WAREHOUSE_SCALE","payloadJson":"{}","createdAt":"2026-09-01T10:00:00"},{"evidenceId":"e2","claimId":"CLAIM-SYN-015","source":"COURIER_VOICE","payloadJson":"{}","createdAt":"2026-09-05T09:00:00"}]
}' | grep -o '"conflictType":"[^"]*"'

echo "=== TEST SUITE COMPLETE ==="
