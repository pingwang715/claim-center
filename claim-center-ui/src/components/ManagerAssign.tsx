import React, { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserPlus } from "@fortawesome/free-solid-svg-icons";
import axios, { AxiosError } from "axios";
import type { ApiErrorResponse } from "../types/apiErrorResponse";
import apiClient from "../api/apiClient";

interface Adjuster {
  adjusterId: number;
  firstName: string;
  lastName: string;
}

interface AssignAdjusterProps {
  claimId: string | undefined;
}

export default function ManagerAssign({
  claimId,
}: AssignAdjusterProps): React.JSX.Element {
  const [isLoading, setIsLoading] = useState(false);
  const [selectedAdjusterId, setSelectedAdjusterId] = useState<string>("");
  const [adjusters, setAdjusters] = useState<Adjuster[]>([]);

  useEffect(() => {
    const loadAdjusters = async (): Promise<void> => {
      try {
        const data = await getAdjusters();
        setAdjusters(data);
      } catch (error) {
        if (error instanceof Response) {
          const message = await error.text();
          toast.error(message || "Failed to load adjusters.");
        } else {
          toast.error("Failed to load adjusters.");
        }
      }
    };

    loadAdjusters();
  }, []);

  const handleAssign = async (): Promise<void> => {
    if (!selectedAdjusterId) {
      toast.error("Please select an adjuster.");
      return;
    }

    setIsLoading(true);
    try {
      await assignAdjusterToClaim(claimId, parseInt(selectedAdjusterId, 10));
      toast.success("Adjuster assigned successfully.");
    } catch (error) {
      if (error instanceof Response) {
        const message = await error.text();
        toast.error(message || "Something went wrong.");
      } else {
        toast.error("An unexpeced error occurred.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col gap-2 w-full">
      <select
        value={selectedAdjusterId}
        onChange={(e) => setSelectedAdjusterId(e.target.value)}
        disabled={isLoading || adjusters.length === 0}
        className="w-full px-2 py-2 border border-gray-300 rounded-md text-sm text-secondary bg-white focus:outline-none focus:ring-2 focus:ring-blue-600 disabled:opacity-50"
      >
        <option value="">
          {adjusters.length === 0
            ? "Loading adjusters..."
            : "Select an adjuster..."}
        </option>
        {adjusters.map((adjuster) => (
          <option key={adjuster.adjusterId} value={adjuster.adjusterId}>
            {adjuster.firstName} {adjuster.lastName}
          </option>
        ))}
      </select>

      <button
        onClick={handleAssign}
        disabled={isLoading || !selectedAdjusterId}
        className="w-full px-4 py-2 bg-blue-700 text-white rounded-md text-lg font-semibold hover:bg-blue-500 transition disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <FontAwesomeIcon icon={faUserPlus} /> Assign
      </button>
    </div>
  );
}

const getAdjusters = async (): Promise<Adjuster[]> => {
  try {
    const response = await apiClient.get<Adjuster[]>("/claims/adjusters", {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
      },
    });
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError<ApiErrorResponse>;
      throw new Response(
        axiosError.response?.data?.errorMessage ??
          axiosError.message ??
          "Failed to fetch adjusters. Please try again.",
        { status: axiosError.status ?? 500 },
      );
    }
    throw new Response("Failed to fetch adjusters. Please try again.", {
      status: 500,
    });
  }
};

const assignAdjusterToClaim = async (
  id: string | undefined,
  adjusterId: number,
): Promise<void> => {
  if (!id) {
    throw new Response("Claim ID is missing.", { status: 400 });
  }

  const parsedId = parseInt(id, 10);
  if (isNaN(parsedId)) {
    throw new Response("Invalid Claim ID.", { status: 400 });
  }

  try {
    const response = await apiClient.post(
      `/claims/${parsedId}/assign`,
      { adjusterId },
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
        },
      },
    );
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError<ApiErrorResponse>;
      throw new Response(
        axiosError.response?.data?.errorMessage ??
          axiosError.message ??
          "Failed to assign adjuster. Please try again.",
        { status: axiosError.status ?? 500 },
      );
    }

    throw new Response("Failed to approve the claim. Please try again,", {
      status: 500,
    });
  }
};
