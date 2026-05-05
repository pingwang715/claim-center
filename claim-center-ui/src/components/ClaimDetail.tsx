import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import apiClient from "../api/apiClient";
import type { ClaimDetail } from "../types/ClaimDetail";
import axios, { AxiosError } from "axios";
import PageTitle from "./PageTitle";
import { STATUS_STYLES, STATUS_LABELS, PAYMENT_STYLES, PAYMENT_LABELS, POLICY_STYLES, POLICY_LABELS } from "../types/statusStyle";

export default function ClaimDetail(): React.JSX.Element {
  const mockClaim = {
    id: 40,
    title: "Flight tickets cancelled last minute",
    description:
      "My flight tickets were cancelled last minute, for MUC - LHR from June 10 - 13.",
    type: "travel",
    claimedAmount: 100,
    payoutAmount: null,
    status: "SUBMITTED",
    paymentStatus: null,
    createdAt: "2026-05-04 15:28:29",
    updatedAt: "2026-05-04 15:28:29",
    closedAt: null,
  };

  return (
    <div className="min-h-[852px] bg-normalbg py-4 px-2 font-primary">
      <PageTitle title={`Claim #0000${mockClaim.id}`} />
      <div className="max-w-[580px] mx-auto">
        <div className="bg-gray-50 border border-gray-200 rounded-xl p-5 shadow-md m-4">
          <h2 className="text-xs font-medium text-gray-400 uppercase tracking-wider mb-4">
            Claim Details
          </h2>
          <div className="divide-y divide-gray-200">
            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Title</span>
                <p className="text-sm text-gray-900 truncate">
                  {mockClaim.title}
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Description</span>
                <p className="text-sm text-gray-900">{mockClaim.description}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Type</span>
                <span
                  className={`text-xs font-medium px-2.5 py-0.5 rounded-full shrink-0 ${POLICY_STYLES[mockClaim.type]}`}
                >
                  {POLICY_LABELS[mockClaim.type]}
                </span>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Claim Status</span>
                <span
                  className={`text-xs font-medium px-2.5 py-0.5 rounded-full shrink-0 ${STATUS_STYLES[mockClaim.status]}`}
                >
                  {STATUS_LABELS[mockClaim.status]}
                </span>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Claim Amount</span>
                <p className="text-sm text-gray-900">{mockClaim.claimedAmount}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Payout Amount</span>
                <p className="text-sm text-gray-900">
                  {mockClaim.payoutAmount ?? "-"}
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Payment Status</span>
                {mockClaim.paymentStatus ? (<span
                  className={`text-xs font-medium px-2.5 py-0.5 rounded-full shrink-0 ${PAYMENT_STYLES[mockClaim.paymentStatus]}`}
                >
                  {PAYMENT_LABELS[mockClaim.paymentStatus]}
                </span>) : <p className="text-sm text-gray-900">-</p>}
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Created At</span>
                <p className="text-sm text-gray-900">{mockClaim.createdAt}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Updated At</span>
                <p className="text-sm text-gray-900">{mockClaim.updatedAt}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 py-3">
              <div className="flex flex-col gap-0.5">
                <span className="text-xs text-gray-400">Closed At</span>
                <p className="text-sm text-gray-900">{mockClaim.closedAt ? mockClaim.closedAt : "-"}</p>

              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  );
}

interface ApiErrorResponse {
  errorMessage?: string;
}

const getClaimDetail = async (id: number): Promise<ClaimDetail> => {
  try {
    const response = await apiClient.get<ClaimDetail>(`/claims/${id}`);
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

    throw (
      new Response("Failed to fetch claims. Please try again,"),
      {
        status: 500,
      }
    );
  }
};
