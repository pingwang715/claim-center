export const STATUS_STYLES: Record<string, string> = {
  SUBMITTED: "bg-blue-100 text-blue-800",
  UNDER_REVIEW: "bg-amber-100 text-amber-800",
  APPROVED: "bg-green-100 text-green-800",
  REJECTED: "bg-red-100 text-red-800",
  OVERRIDDEN_APPROVED: "bg-green-100 text-green-800",
  OVERRIDDEN_REJECTED: "bg-red-100 text-red-800",
};

export const STATUS_LABELS: Record<string, string> = {
  SUBMITTED: "Submitted",
  UNDER_REVIEW: "Under review",
  APPROVED: "Approved",
  REJECTED: "Rejected",
  OVERRIDDEN_APPROVED: "Overridden approved",
  OVERRIDDEN_REJECTED: "Overridden rejected",
};

export const PAYMENT_STYLES: Record<string, string> = {
  PENDING: "bg-gray-100 text-gray-800",
  PROCESSING: "bg-blue-100 text-blue-800",
  PAID: "bg-green-100 text-green-800",
  FAILED: "bg-red-100 text-red-800",
};

export const PAYMENT_LABELS: Record<string, string> = {
  PENDING: "Pending",
  PROCESSING: "Processing",
  PAID: "Paid",
  FAILED: "Failed",
};

export const POLICY_STYLES: Record<string, string> = {
  health: "bg-green-100 text-green-800",
  car: "bg-purple-100 text-purple-800",
  travel: "bg-red-100 text-red-800",
  pet: "bg-amber-100 text-amber-800",
  property: "bg-blue-100 text-blue-800",
};

export const POLICY_LABELS: Record<string, string> = {
  health: "Health",
  car: "Auto",
  travel: "Travel",
  pet: "Pet",
  property: "Property",
};
