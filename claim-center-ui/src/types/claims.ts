export interface Claim {
  id: number;
  title: string;
  status: "SUBMITTED" | "UNDER_REVIEW" | "APPROVED" | "REJECTED" | "OVERRIDDEN_APPROVED" | "OVERRIDDEN_REJECTED";

}
