import React, { useState } from "react";
import apiClient from "../api/apiClient";
import axios, { AxiosError } from "axios";
import type { ApiErrorResponse } from "../types/apiErrorResponse";

interface AdjusterRejectProps {
  claimId: number;
}

export default function AdjusterReject({
  claimId,
}: AdjusterRejectProps): React.JSX.Element {
  const [isLoading, setIsLoading] = useState(false);

  const handleReject = async (): Promise<void> => {
    setIsLoading(true);
    try {
      await rejectClaimAdjuster(claimId);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <button
      onSubmit={handleReject}
      disabled={isLoading}
      className="w-full px-4 py-2 bg-primary text-white rounded-md text-lg font-semibold hover:bg-dark transition"
    >
      Reject
    </button>
  );
}

const rejectClaimAdjuster = async (id: number) => {
  try {
    const response = await apiClient.post(`/claims/${id}/reject`);
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
      new Response("Failed to reject the claim. Please try again,"),
      {
        status: 500,
      }
    );
  }
};
