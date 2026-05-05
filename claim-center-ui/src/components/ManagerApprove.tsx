import React, {useState} from "react";
import apiClient from "../api/apiClient";
import axios, { AxiosError } from "axios";
import type {ApiErrorResponse} from "../types/apiErrorResponse";

interface ManagerApproveProps {
  claimId: number,
  approve: boolean,
}

export default function ManagerApprove({claimId, approve}: ManagerApproveProps): React.JSX.Element {
  const [isLoading, setIsLoading] = useState(false);

  const handleOverrideApprove = async (): Promise<void> => {
    setIsLoading(true);
    try {
      await overrideApproveManager(claimId, true);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <button onClick={handleOverrideApprove} disabled={isLoading} className="w-full px-4 py-2 bg-primary text-white rounded-md text-lg font-semibold hover:bg-dark transition">
      Approve
    </button>
  );
}

const overrideApproveManager = async (id: number, approve: boolean): Promise<void> => {
  try {
    const response = await apiClient.post(`/claims/${id}/override`, {approve});
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
      new Response("Failed to override the claim. Please try again,"),
      {
        status: 500,
      }
    );
  }
}
