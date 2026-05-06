import React, { useState, useEffect } from "react";
import { Link, useParams} from "react-router-dom";
import apiClient from "../api/apiClient";
import type { ClaimDetail } from "../types/ClaimDetail";
import axios, { AxiosError } from "axios";
import PageTitle from "./PageTitle";
import { STATUS_STYLES, STATUS_LABELS, PAYMENT_STYLES, PAYMENT_LABELS, POLICY_STYLES, POLICY_LABELS } from "../types/statusStyle";
import type { ApiErrorResponse } from "../types/apiErrorResponse";
import { formatDate } from "../types/date";
import { useAuth } from "../store/auth-context";
import AdjusterApprove from "./AdjusterApprove";
import AdjusterReject from "./AdjusterReject";
import ManagerApprove from "./ManagerApprove";
import ManagerReject from "./ManagerReject";
import ManagerAssign from "./ManagerAssign";

export default function ClaimDetail(): React.JSX.Element {
  const {user} = useAuth();
  const params = useParams();
  const [claim, setClaim] = useState<ClaimDetail | null>(null);

  useEffect(() => {
    getClaimDetail(params.claimId).then((data) => setClaim(data));
  }, [params.claimId]);

  if (!claim) {
    return (
      <div className="flex items-center justify-center min-h-[820px] bg-normalbg">
        <span className="text-4xl font-semibold text-primary">
          Loading...
        </span>
      </div>
    )
  }

  return (
    <div className="min-h-[852px] bg-normalbg py-4 px-2 font-primary">
      <PageTitle title={`Claim #0000${claim.claimId}`} />
      <div className="max-w-[580px] mx-auto">
        <Link to="/claims" className="mx-5 text-sm font-semibold text-primary hover:text-dark">⬅️ Back to claims</Link>
        {user?.role === "ROLE_ADJUSTER" && (<div className="flex flex-col-2 gap-3 bg-normalbg m-4">
          <AdjusterApprove claimId={String(claim.claimId)} />
          <AdjusterReject claimId={String(claim.claimId)} />
        </div>)}
        {user?.role === "ROLE_MANAGER" && (<div className="flex flex-col-3 gap-3 bg-normalbg m-4">
          <ManagerAssign claimId={String(claim.claimId)} adjusterId="16" />
          <ManagerApprove claimId={String(claim.claimId)} approve={true} />
          <ManagerReject claimId={String(claim.claimId)} approve={false} />
        </div>)}
        <div className="bg-gray-50 border border-gray-200 rounded-xl p-5 shadow-md m-4">
          <h2 className="text-xs font-medium text-gray-400 uppercase tracking-wider mb-4">
            Claim Details
          </h2>
          <div className="divide-y divide-gray-200">
            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Title</span>
                <p className="text-sm text-gray-900 truncate">
                  {claim.title}
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Description</span>
                <p className="text-sm text-gray-900">{claim.description}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Type</span>
                <span
                  className={`text-xs font-medium px-2.5 py-0.5 rounded-full shrink-0 ${POLICY_STYLES[claim.type]}`}
                >
                  {POLICY_LABELS[claim.type]}
                </span>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Claim Status</span>
                <span
                  className={`text-xs font-medium px-2.5 py-0.5 rounded-full shrink-0 ${STATUS_STYLES[claim.status]}`}
                >
                  {STATUS_LABELS[claim.status]}
                </span>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Claim Amount</span>
                <p className="text-sm text-gray-900">{claim.claimedAmount}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Payout Amount</span>
                <p className="text-sm text-gray-900">
                  {claim.payoutAmount ?? "-"}
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Payment Status</span>
                {claim.paymentStatus ? (<span
                  className={`text-xs font-medium px-2.5 py-0.5 rounded-full shrink-0 ${PAYMENT_STYLES[claim.paymentStatus]}`}
                >
                  {PAYMENT_LABELS[claim.paymentStatus]}
                </span>) : <p className="text-sm text-gray-900">-</p>}
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Created At</span>
                <p className="text-sm text-gray-900">{formatDate(claim.createdAt)}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Updated At</span>
                <p className="text-sm text-gray-900">{formatDate(claim.updatedAt)}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Closed At</span>
                <p className="text-sm text-gray-900">{claim.closedAt ? formatDate(claim.closedAt) : "-"}</p>

              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  );
}

const getClaimDetail = async (id: string | undefined): Promise<ClaimDetail> => {
  if (!id) {
    throw new Response("Claim ID is missing.", { status: 4000 });
  }

  const parsedId = parseInt(id, 10);
  if (isNaN(parsedId)) {
    throw new Response("Invalid Claim ID.", { status: 400 });
  }

  try {
    const response = await apiClient.get<ClaimDetail>(`/claims/${parsedId}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("jwtToken")}` },
    });
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError<ApiErrorResponse>;
      throw new Response(
        axiosError.response?.data?.errorMessage ??
          axiosError.message ??
          "Failed to fetch claims. Please try again.",
        { status: axiosError.status ?? 500 },
      );
    }

    throw new Response("Failed to fetch claims. Please try again,",
      {
        status: 500,
      });
  }
};
