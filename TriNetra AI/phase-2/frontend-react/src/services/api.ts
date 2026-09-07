import axios from 'axios';

const V3_API = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export interface ClaimSummary {
  claimId: string;
  caseId?: string;
  customerId?: string;
  orderId: string;
  product?: string;
  status: string;
  state?: string;
  outboundWeightGrams?: number;
  returnWeightGrams?: number;
  updatedAt?: string;
  verificationId?: string;
  unitId?: string;
}

export interface ClaimDetail extends ClaimSummary {
  outboundSku?: string;
  returnSku?: string;
  differencePercent?: number;
  conflictType?: string;
  reasoning?: string;
  generatedAt?: string;
  evidence?: Array<{ evidenceId: string; source: string; payloadJson: string; createdAt: string }>;
  expectedSerial?: string;
  observedSerial?: string;
  expectedImei?: string;
  observedImei?: string;
  checkpoints?: Array<{ checkpointId: string; sequenceNumber: number; observedSerial: string; observedImei?: string; decision: string; location?: string }>;
  custody?: Array<{ custodyEventId: string; fromActor?: string; toActor: string; action: string; location?: string; occurredAt: string }>;
  audit?: Array<{ auditEventId: string; eventType: string; actorId: string; createdAt: string }>;
}

export const fetchClaims = async (status?: string): Promise<{ claims: ClaimSummary[]; total: number }> => {
  try {
    const res = await axios.get<Array<{ verificationId: string; unitId: string; operatorId: string; state: string; createdAt: string; updatedAt: string }>>(`${V3_API}/verifications`);
    const claims = res.data.map(item => ({
      claimId: item.verificationId,
      caseId: item.verificationId,
      orderId: item.unitId,
      product: 'Serialized unit',
      status: item.state,
      state: item.state,
      verificationId: item.verificationId,
      unitId: item.unitId,
      updatedAt: item.updatedAt,
    }));
    const filtered = status && status !== 'ALL'
      ? claims.filter(claim => claim.state === status)
      : claims;
    return {
      claims: status && status !== 'ALL' ? claims.filter(claim => claim.state === status) : claims,
      total: filtered.length,
    };
  } catch (error) {
    console.error('Failed to fetch claims:', error);
    return { claims: [], total: 0 };
  }
};

export const fetchClaimDetail = async (claimId: string): Promise<ClaimDetail | null> => {
  try {
    const res = await axios.get(`${V3_API}/verifications/${claimId}/investigation`);
    const verification = res.data.verification;
    const checkpoints = res.data.checkpoints || [];
    const latest = checkpoints[checkpoints.length - 1];
    return {
      claimId,
      caseId: claimId,
      orderId: verification.unitId,
      product: 'Serialized unit',
      status: verification.state,
      state: verification.state,
      expectedSerial: latest?.expectedSerial,
      observedSerial: latest?.observedSerial,
      expectedImei: latest?.expectedImei,
      observedImei: latest?.observedImei,
      checkpoints,
      custody: res.data.custody || [],
      audit: res.data.audit || [],
    };
  } catch (error) {
    console.error(`Failed to fetch claim detail for ${claimId}:`, error);
    return null;
  }
};

export const submitVerdictOverride = async (
  claimId: string,
  verdict: string,
  reasoning: string,
  investigatorId = '00000000-0000-0000-0000-000000000001'
) => {
  return Promise.reject(new Error('Investigator override is not implemented by the current backend.'));
};
