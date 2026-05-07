import React, { useState } from "react";
import apiClient from "../api/apiClient";
import axios, { AxiosError } from "axios";
import type {ApiErrorResponse} from "../types/apiErrorResponse";
import {toast} from "react-toastify";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleCheck } from "@fortawesome/free-solid-svg-icons";

interface AdjusterApproveProps {
  claimId: string | undefined;
}

export default function AdjusterApprove({
  claimId,
}: AdjusterApproveProps): React.JSX.Element {
  const [isLoading, setIsLoading] = useState(false);

  const handleApprove = async (): Promise<void> => {
    setIsLoading(true);
    try {
      await approveClaimAdjuster(claimId);
      toast.success("Claim approved successfully.")
    } catch (error) {
      if (error instanceof Response) {
        if (error.status === 409) {
          toast.error("This claim has already been processed and cannot be modified.")
        } else {
          const message = await error.text();
          toast.error(message || "Something went wrong.")
        }
      } else {
        toast.error("An unexpeced error occurred.")
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <button
      onClick={handleApprove}
      disabled={isLoading}
      className="w-full px-4 py-2 bg-green-700 text-white rounded-md text-lg font-semibold hover:bg-green-500 transition"
    >
      <FontAwesomeIcon icon={faCircleCheck} />Approve
    </button>
  );
}

const approveClaimAdjuster = async (id: string | undefined): Promise<void> => {
  if (!id) {
    throw new Response("Claim ID is missing.", { status: 400 });
  }

  const parsedId = parseInt(id, 10);
  if (isNaN(parsedId)) {
    throw new Response("Invalid Claim ID.", { status: 400 });
  }
  try {
    const response = await apiClient.post(`/claims/${parsedId}/approve`, {}, {
      headers: { Authorization: `Bearer ${localStorage.getItem("jwtToken")}` },
    });
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

    throw new Response("Failed to approve the claim. Please try again,",
      {
        status: 500,
      });
  }
};
