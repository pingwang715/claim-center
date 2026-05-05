import type { ClaimStatus } from "./claimStatus";
import type { PaymentStatus } from "./paymentStatus";
import type { PolicyType } from "./policyTypes";

export interface ClaimDetail {
  claimId: number;
  title: string;
  description: string;
  type: PolicyType;
  claimedAmount: number;
  payoutAmount: number | null;
  status: ClaimStatus;
  paymentStatus: PaymentStatus | null;
  createdAt: string;
  updatedAt: string;
  closedAt: string | null;
}
