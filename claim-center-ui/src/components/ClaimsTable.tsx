import React from "react";
import type { Claim } from "../types/claims";

interface Props {
  claims: Claim[];
}

const STATUS_STYLES: Record<Claim["status"], string> = {
  submitted: "bg-blue-100 text-blue-800",
  under_review: "bg-amber-100 text-amber-800",
  approved: "bg-green-100 text-green-800",
  rejected: "bg-red-100 text-red-800",
  overridden_approved: "bg-green-100 text-green-800",
  overridden_rejected: "bg-red-100 text-red-800",
};

const STATUS_LABELS: Record<Claim["status"], string> = {
  submitted: "Submitted",
  under_review: "Under review",
  approved: "Approved",
  rejected: "Rejected",
  overridden_approved: "Overriden approved",
  overridden_rejected: "Overriden rejected",
};

export const ClaimsTable: React.FC<Props> = ({ claims }) => {
  if (claims.length === 0) {
    return (
      <div className="bg-white border border-gray-200 rounded-xl p-5">
        <h2 className="text-xs font-medium text-gray-400 uppercase tracking-wider mb-4">
          My claims
        </h2>
        <p className="text-sm text-gray-400 text-center py-6">
          No claims found
        </p>
      </div>
    );
  }

  return (
    <div className="min-w-[520px] mt-8">
      <div className="bg-gray-50 border border-gray-200 rounded-xl p-5 shadow-md">
        <h2 className="text-xs font-medium text-gray-400 uppercase tracking-wider mb-4">
          My claims
        </h2>
        <ul className="divide-y divide-gray-100">
          {claims.map((claim) => (
            <li key={claim.id} className="flex items-center gap-3 py-3">
              <div className="flex-1 min-w-0 text-left">
                <p className="text-sm text-gray-900 truncate">{claim.title}</p>
              </div>

              <span className={`text-xs font-medium px-2.5 py-0.5 rounded-full shrink-0 ${STATUS_STYLES[claim.status]}`}>
                {STATUS_LABELS[claim.status]}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};
