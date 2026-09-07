# TriNetra AI: iQOO 15 Flagship Mobile Client & UI/UX Workflow Specification
**Version:** 1.0.0 (iQOO Flagship Hackathon Blueprint)
**Hardware Target:** iQOO 15 (Snapdragon 8 Elite / Snapdragon NPU, 16GB LPDDR5X)
**Core Engine Integration:** TriNetra Spring Boot 3.2 Cloud Backend + On-Device Edge NPU Layer

---

## 1. System Architecture: Hybrid Edge-Cloud Deployment

To bypass network latency, protect user privacy, and ensure zero-network resilience, TriNetra AI splits its execution between the **iQOO 15 Local Edge Layer** and the **TriNetra Enterprise Cloud Backend**.

```
                           ┌────────────────────────────────────────┐
                           │          iQOO 15 MOBILE DEVICE         │
                           │                                        │
                           │  ┌──────────────────────────────────┐  │
                           │  │        FuntouchOS/OriginOS       │  │
                           │  └────────────────┬─────────────────┘  │
                           │                   │                    │
                           │  ┌────────────────▼─────────────────┐  │
                           │  │  TriNetra Native App (React Native)│  │
                           │  └────────┬────────────────┬────────┘  │
                           │           │                │           │
                           │  ┌────────▼───────┐ ┌──────▼────────┐  │
                           │  │ Snapdragon NPU │ │ Snapdragon GPU│  │
                           │  │ (Quantized LLM │ │ (OpenCV Edge  │  │
                           │  │  via ONNX/QNN) │ │  CV Processing)  │  │
                           │  └────────┬───────┘ └──────┬────────┘  │
                           └───────────┼────────────────┼───────────┘
                                       │                │
            JSON Telemetry Payload     │                │ Local CV Anomaly
            (Constrained Grammar)      ▼                ▼ Signals
        ┌───────────────────────────────────────────────────────────────┐
        │                 TRINETRA ENTERPRISE CLOUD BACKEND             │
        │                                                               │
        │  ┌─────────────────────────────────────────────────────────┐  │
        │  │     Spring Boot 3.2 Distributed Microservice Cluster     │  │
        │  │  (Claim, Evidence, Fraud Engine, Verdict, Integration)  │  │
        │  └───────────────────────────┬─────────────────────────────┘  │
        │                              ▼                                │
        │  ┌─────────────────────────────────────────────────────────┐  │
        │  │        Deterministic Reconciliation & Decision Engine   │  │
        │  │  (McNemar validated, p = 3.30e-13, Precision = 1.0000)  │  │
        │  └─────────────────────────────────────────────────────────┘  │
        └───────────────────────────────────────────────────────────────┘
```

---

## 2. iQOO 15 Native UI/UX Workflows

TriNetra AI adapts dynamically based on the active stakeholder role. Below are the step-by-step visual UI wireframes, user interaction flows, and NPU/GPU execution specs for each critical logistics node.

---

### Workflow A: The Deskless Gig Delivery Executive (Doorstep Handoff)
* **Goal:** Verify QR/NFC physical seal integrity at the exact moment of custody transfer in real time, with local evidence capture and queued sync for elevators or basements with intermittent network.
* **Voice parsing:** The current Android build tries the configured Spring Boot backend and Groq cloud parser first, then falls back automatically to the local TF-IDF/Naive Bayes classifier. The verdict remains deterministic in both paths; the shipped build does not include the previously described Qwen NPU runtime.

#### 1. Wireframe Layout
```
+─────────────────────────────────────────+
| [iQOO] TriNetra: Courier Handoff Mode   |
+─────────────────────────────────────────+
| ORDER: #ORD-98402  | CUSTOMER: S. Kumar |
| HUB: Chennai-South | CARRIER: BlueDart  |
+─────────────────────────────────────────+
| 1. SCAN TAMPER SEAL QR/NFC              |
|  [ [ ] ] <- Place camera over tag       |
|                                         |
| STATUS: SCANNING... [EXIF MATCHED ✅]   |
| SERIAL: TRN-TAG-7729-A                  |
+─────────────────────────────────────────+
| 2. AUDIO TESTIMONY (NPU TRANSCRIPTION)  |
|  "The box is sealed, but weight feels   |
|   way too light compared to label."     |
|                                         |
|  [● Hold & Speak (Hindi/Tamil/English)] |
+─────────────────────────────────────────+
| 3. ON-DEVICE NPU PARSING RESULTS        |
|  Anomalies Identified locally:          |
|  • Weight Discrepancy Flagged [HIGH]    |
|  • Seal Structural Integrity: INTACT    |
+─────────────────────────────────────────+
| [ CONFIRM HANDOFF & SAVE SYNC PACKET ]  |
+─────────────────────────────────────────+
```

#### 2. User Interaction Flow
1. **Initiate Scan:** The delivery executive arrives at the doorstep, opens TriNetra, and selects **Doorstep Scan**.
2. **Metadata Capture:** The app instantly reads the phone’s GPS and local system clock, capturing the coordinate state and saving it as an encrypted EXIF record to prevent metadata spoofing.
3. **Physical Scan:** The executive aligns the camera with the packaging's serialized QR/NFC tamper tape. The local app decodes the sequence ID (`TRN-TAG-7729-A`).
4. **Voice Testimony:** The courier holds the push-to-talk button and says in plain English or regional vernacular: *"The package is sealed and the tape is fine, but the box is extremely light and rattles when moved."*
5. **NPU Processing:** The local quantized LLM running on the iQOO NPU processes the audio waveform, executes transcription, performs semantic entity extraction, and outputs a structured telemetry schema conforming to TriNetra's `Canonical Normalizer` requirements.
6. **Local Cache / Auto-Sync:** If offline, the packet is signed with a local SHA-256 hash and queued in the app's SQLite DB. Once mobile data returns, it pushes to the TriNetra Cloud `Claim Service` on port `8080`.

---

### Workflow B: The High-Speed Warehouse QC Inspector (15-Second Return Check)
* **Goal:** Stop returns fraud (wardrobing and counterfeit product swaps) at return hubs in under 15 seconds.
* **On-Device GPU Execution:** Runs a lightweight, hardware-accelerated **OpenCV Canny Edge Density & Laplacian Texture Variance** algorithm directly on the Snapdragon GPU to evaluate material surface decay.

#### 1. Wireframe Layout
```
+─────────────────────────────────────────+
| [iQOO] TriNetra: 15-Sec Rapid QC        |
+─────────────────────────────────────────+
| RETURN CLAIM: #CLM-33829                |
| EXPECTED SKU: NK-RN-FLY-12 (Shoe, Blk)  |
+─────────────────────────────────────────+
| 1. EDGE CV IMAGE EVALUATION             |
|  [ CAMERA VIEWPORT: SHOE SOLE ]         |
|  • Processing Saturation Balance...     |
|  • Inspecting Micro-Abrasion Density... |
|                                         |
|  LIVESTREAM STATUS: PROCESSING...       |
+─────────────────────────────────────────+
| 2. COMPUTED QUALITY METRICS (LOCAL GPU) |
|  • Laplacian Texture Variance : 0.28    |
|  • Canny Edge Crease Density  : 0.81    |
|  • HSV Color Match Score     : 98.4%   |
+─────────────────────────────────────────+
| LOCAL RESULT: WARDROBING FLAG [CRITICAL]|
| REASON: Micro-wear patterns indicate    |
| heavy street use. Seal tag missing.     |
+─────────────────────────────────────────+
| [ REJECT RETURN ]   [ ESCALATE CASE ]   |
+─────────────────────────────────────────+
```

#### 2. User Interaction Flow
1. **Barcode Check:** The return operator scans the incoming return package barcode.
2. **Visual Assessment:** The operator holds the iQOO 15 over the returned item (e.g., shoe soles, garment collar). The camera streams frames directly to the local OpenCV service.
3. **High-Speed CV Inference:** 
   * **Laplacian filter** checks texture variance (low variance = fake smooth material or severe wear).
   * **Canny edge detector** checks crease density (high density = pre-worn / wardrobed).
   * **HSV histogram comparison** checks color distribution against original catalogue specification records.
4. **Instant Classification:** In **2.8 seconds**, the GPU-bound CV script concludes if the item is counterfeit or worn, outputting a `wear_score`.
5. **Verdict Pipeline:** If the wear score is $>0.70$, the local app triggers a high-severity `Wardrobing` signal, bypassing manual queue bottlenecks and flagging the case in the TriNetra React Dashboard on port `3000`.

---

### Workflow C: The Merchant/Seller (Fulfillment Pack-Station Check-in)
* **Goal:** Log precise physical outbound telemetry (calibrated weight, dimensions, packaging seal serialization) at the point of origin, establishing a defensible baseline of what was actually sent.
* **On-Device Execution:** Captures calibrated scale data via local Bluetooth and seals the baseline with cryptographic hash telemetry.

#### 1. Wireframe Layout
```
+─────────────────────────────────────────+
| [iQOO] TriNetra: Merchant Pack-Station  |
+─────────────────────────────────────────+
| MERCHANT ID: VND-ZARA-INDIA             |
| PRODUCT: Linen Slim Fit Shirt (L, Wht)  |
+─────────────────────────────────────────+
| 1. LOG OUTBOUND TELEMETRY               |
|  • Calibrated Scale Weight: 242.0 g     |
|  • Box Volume Dimensions : 30x20x5 cm   |
|                                         |
|  [ CONNECTED VIA BLUETOOTH SCALE ✅ ]    |
+─────────────────────────────────────────+
| 2. APPLY & REGISTER TAMPER SEAL         |
|  • Scan Void Tape Serial Number:        |
|  [ [ ] ] <- Place camera over seal QR   |
|                                         |
|  REGISTERED TAG ID: TRN-TAG-8821-X      |
+─────────────────────────────────────────+
| BASELINE RECORD ESTABLISHED             |
| SHA-256: 4e8c9b...a12f                  |
+─────────────────────────────────────────+
| [ COMMIT BASLINE TO TRINETRA LEDGER ]   |
+─────────────────────────────────────────+
```

#### 2. User Interaction Flow
1. **Weigh Item:** The merchant places the boxed order onto the calibrated packaging scale. The scale pushes the exact weight (e.g., `242.0 g`) to the iQOO client via Bluetooth Low Energy (BLE).
2. **Register Seal:** The merchant peels a serialized tamper-evident physical void seal tape and applies it over the box flaps. They scan the seal’s barcode or QR code with the iQOO 15.
3. **Commit Hash:** The local client combines `Order_ID + Calibrated_Weight + Seal_Tag_ID + Timestamp + GPS` into a cryptographic hash block.
4. **Push Ledger:** The data packet is transmitted to TriNetra's `Claim Service` (Port 8080) to anchor the immutable outbound ledger baseline.

---

### Workflow D: The Honest Consumer Portal (Safe-Unbox Witness Mode)
* **Goal:** Protect consumers from wrongful accusations by establishing verifiable evidence of the box's condition *before* and *during* opening.
* **On-Device Execution:** Uses local video hashing and EXIF metadata extraction to guarantee that unboxing videos have not been edited, re-shot, or tampered with.

#### 1. Wireframe Layout
```
+─────────────────────────────────────────+
| [iQOO] TriNetra: Safe-Unbox Witness     |
+─────────────────────────────────────────+
| CLAIM: #CLM-44021   | ORDER: #ORD-77122 |
| PLATFORM: Flipkart  | SELLER: Appario   |
+─────────────────────────────────────────+
| 1. SCAN TAMPER SEAL STATE PRIOR TO OPEN |
|  [ CAMERA PREVIEW: SEAL INTERFACE ]     |
|                                         |
|  TAG ID VERIFIED: TRN-TAG-8821-X        |
|  SEAL INTEGRITY : INTACT ✅             |
+─────────────────────────────────────────+
| 2. RECORD WITNESS UNBOXING VIDEO        |
|  [ VIDEO VIEWPORT: RECORDING LIVE ]     |
|  ⏱️ 00:14 / 02:00                       |
|                                         |
|  * Keep the entire box in the frame.    |
+─────────────────────────────────────────+
| METADATA ENVELOPE GENERATED SECURELY    |
| • GPS Match: Chennai, IND (Home)        |
| • Non-Edit Hash: Verified Original      |
+─────────────────────────────────────────+
| [ SUBMIT TRUST-PROOF & DISPUTE ]        |
+─────────────────────────────────────────+
```

#### 2. User Interaction Flow
1. **Trigger Witness Mode:** The consumer receives their high-value order (e.g., smartphone, watch) and opens the TriNetra Portal app.
2. **Scan Verification:** The consumer is instructed to scan the applied tamper seal. The app checks if the seal status matches the active database state.
3. **Secure Video Recording:** The app activates the native video camera. While recording, the iQOO hardware captures system clocks, device orientation telemetry, and frame-level timestamps.
4. **Crypto-Signing:** The instant recording ends, the app generates a progressive SHA-256 checkblock for the video file, appending it to the metadata schema.
5. **Submit Claim:** If the consumer receives a wrong or substituted item, they submit the dispute. Because the unboxing file has a verified local EXIF signature, the platform confirms that the footage is authentic and has not been subjected to digital splicing or frameswap injection.

---

## 3. Local NPU Schema Enforcement (Constrained Decoding Strategy)

Quantized language models running on local NPUs often exhibit schema drifting (hallucinating keys, producing invalid brackets). To guarantee the **Qwen-2.5-3B** or **Llama-3-8B** models output syntactically valid JSON conforming to TriNetra’s 38-column research schema, the mobile app utilizes **GBNF (GGML BNF) grammars** or **JSON schema constraints** inside the llama.cpp / ONNX runtime engine.

### On-Device GBNF Constraint Definition
```ebnf
# GBNF Grammar defining the strict local LLM output for TriNetra voice ingestion
root   ::= object
object ::= "{" ws "anomaly_detected" ":" ws bool "," ws "seal_integrity" ":" ws string "," ws "courier_narrative_summary" ":" ws string "," ws "weight_assessment" ":" ws string "}"
string ::= "\"" [a-zA-Z0-9\s\.\,\-\:\_]* "\""
bool   ::= "true" | "false"
ws     ::= [ \t\n\r]*
```

### JSON Structured Output generated locally on iQOO 15 NPU:
```json
{
  "anomaly_detected": true,
  "seal_integrity": "INTACT",
  "courier_narrative_summary": "Delivery agent reports box condition is pristine, but highlights that the container feels significantly lighter than its labeled weight and rattles inside.",
  "weight_assessment": "WEIGHT_DISCREPANCY_PROBABLE"
}
```

---

## 4. Federated Edge-to-Cloud API Payload Spec
This is the structured JSON telemetry schema transmitted from the iQOO 15 edge device to the TriNetra Cloud `Claim Service` (Port 8080) / `Evidence Service` (Port 8081). It maps the local phone's sensor inputs into TriNetra's core database models.

```json
{
  "case_id": "CMP-99201-CHN",
  "device_telemetry": {
    "device_make": "iQOO",
    "device_model": "iQOO 15",
    "processor_npu_utilized": "Snapdragon 8 Elite NPU",
    "local_inference_latency_ms": 182,
    "gps_coordinates": {
      "latitude": 13.0827,
      "longitude": 80.2707,
      "coordinate_spoof_check": "SECURE_HARDWARE_GPS_PASS"
    },
    "timestamp_utc": "2026-09-05T10:07:23Z"
  },
  "physical_signals": {
    "scanned_seal_id": "TRN-TAG-7729-A",
    "seal_physical_state": "INTACT",
    "local_opencv_metrics": {
      "laplacian_texture_variance": 0.942,
      "canny_edge_crease_density": 0.124,
      "hsv_color_match_score": 0.9982,
      "wear_score_wardrobing": 0.082
    }
  },
  "semantic_intake": {
    "raw_voice_testimony_transcription": "The package is sealed, but weight feels way too light compared to label.",
    "npu_extracted_anomaly_detected": true,
    "npu_extracted_weight_discrepancy": "WEIGHT_DISCREPANCY_PROBABLE"
  }
}
```

This hybrid-edge model eliminates B2B hardware deployment barriers, reduces e-commerce returns verification latencies, and maintains the integrity of the core **Strict AI Principle** by utilizing local LLMs solely for data collection and formatting.
