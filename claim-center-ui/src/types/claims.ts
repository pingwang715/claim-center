export interface Claim {
  id: number;
  title: string;
  status: "submitted" | "under_review" | "approved" | "rejected" | "overridden_approved" | "overridden_rejected";

}
