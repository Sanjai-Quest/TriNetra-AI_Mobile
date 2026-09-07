import axios from 'axios';

const CLAIM_API = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/claims';

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
}

export interface ClaimDetail extends ClaimSummary {
  outboundSku?: string;
  returnSku?: string;
  differencePercent?: number;
  conflictType?: string;
  reasoning?: string;
  generatedAt?: string;
  evidence?: Array<{ evidenceId: string; source: string; payloadJson: string; createdAt: string }>;
}

export const fetchClaims = async (status?: string): Promise<{ claims: ClaimSummary[]; total: number }> => {
  try {
    const res = await axios.get<ClaimSummary[]>(CLAIM_API);
    const claims = status && status !== 'ALL'
      ? res.data.filter(claim => claim.state === status || claim.status === status)
      : res.data;
    return {
      claims,
      total: claims.length,
    };
  } catch (error) {
    console.error('Failed to fetch claims:', error);
    return { claims: [], total: 0 };
  }
};

export const fetchClaimDetail = async (claimId: string): Promise<ClaimDetail | null> => {
  try {
    const res = await axios.get(`${CLAIM_API}/${claimId}`);
    return res.data;
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
