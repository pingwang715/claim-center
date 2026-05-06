import React, { useState, useEffect } from "react";
import {toast} from "react-toastify";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserPlus } from "@fortawesome/free-solid-svg-icons";
import axios, {AxiosError} from "axios";
import type {ApiErrorResponse} from "../types/apiErrorResponse";
import apiClient from "../api/apiClient";

interface AdjusterApproveProps {
  claimId: string | undefined;
  adjusterId: string | undefined;
}

export default function ManagerAssign({claimId, adjusterId}: AdjusterApproveProps): React.JSX.Element {
  const [isLoading, setIsLoading] = useState(false);

  const handleAssign = async (): Promise<void> => {
    setIsLoading(true);
    try {
      await assignClaim(claimId, adjusterId);
      toast.success("Claim assigned successfully.")
    } catch (error) {
      if (error instanceof Response) {
        const message = await error.text();
        toast.error(message || "Something went wrong.")
      } else {
        toast.error("An unexpeced error occurred.")
      }
    } finally {
      setIsLoading(false);
    }
  };

    return (
      <button
        onClick={handleAssign}
        disabled={isLoading}
        className="w-full px-4 py-2 bg-blue-800 text-white rounded-md text-lg font-semibold hover:bg-blue-500 transition"
      >
        <FontAwesomeIcon icon={faUserPlus} /> Assign
      </button>
    );
}

const assignClaim = async (id: string | undefined, adjusterId: string | undefined): Promise<void> => {
  if (!id) {
    throw new Response("Claim ID is missing.", { status: 400 });
  }

  const parsedId = parseInt(id, 10);
  if (isNaN(parsedId)) {
    throw new Response("Invalid Claim ID.", { status: 400 });
  }

  if (!adjusterId) {
    throw new Response("Adjuster ID is missing.", { status: 400 })
  }

  const parsedAdjusterId = parseInt(adjusterId, 10);
  if (isNaN(parsedAdjusterId)) {
    throw new Response("Invalid Adjuster ID.", { status: 400 });
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
