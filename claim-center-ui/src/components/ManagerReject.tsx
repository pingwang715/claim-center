import React, {useState} from "react";
import apiClient from "../api/apiClient";
import axios, { AxiosError } from "axios";
import type {ApiErrorResponse} from "../types/apiErrorResponse";
import {toast} from "react-toastify";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleXmark } from "@fortawesome/free-solid-svg-icons";

interface ManagerApproveProps {
  claimId: string | undefined;
  approve: boolean;
}

export default function ManagerReject({claimId, approve}: ManagerApproveProps): React.JSX.Element {
  const [isLoading, setIsLoading] = useState(false);

  const handleOverrideApprove = async (): Promise<void> => {
    setIsLoading(true);
    try {
      await overrideRejectManager(claimId, false);
      toast.success("Claim overriden successfully. The claim has been rejected.")
    } catch (error) {
      if (error instanceof Response) {
        const message = error.text();
        toast.error(message || "Something went wrong.")
      }
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <button onClick={handleOverrideApprove} disabled={isLoading} className="w-full px-4 py-2 bg-red-800 text-white rounded-md text-lg font-semibold hover:bg-red-500 transition">
      <FontAwesomeIcon icon={faCircleXmark} /> Reject
    </button>
  );
}

const overrideRejectManager = async (id: string | undefined, approve: boolean): Promise<void> => {
  try {
    const response = await apiClient.post(`/claims/${id}/override`, {approve}, {
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

    throw new Response("Failed to override the claim. Please try again,",
      {
        status: 500,
      });
  }
}
