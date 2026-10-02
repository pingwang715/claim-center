import axios, { AxiosError } from "axios";
import apiClient from "./apiClient";
import type { ApiErrorResponse } from "../types/apiErrorResponse";

export interface ClaimAssessmentResponse {
  riskScore: number;
  summary: string;
  fraudIndicators: string[];
  recommendedAction: string;
}

export async function runAssessment(
  id: string | undefined,
): Promise<ClaimAssessmentResponse> {
  if (!id) {
    throw new Response("Claim ID is missing.", { status: 400 });
  }

  const parsedId = parseInt(id, 10);
    if (isNaN(parsedId)) {
      throw new Response("Invalid Claim ID.", { status: 400 });
  }

  try {
    const response = await apiClient.post(`/claims/${parsedId}/ai-assessment`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
        },
      },
      { timeout: 60000 },
    );

    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError<ApiErrorResponse>;
      throw new Response(
        axiosError.response?.data?.errorMessage ??
          axiosError.message ??
          "Failed to run AI assessment. Please try again.",
        { status: axiosError.status ?? 500 },
      );
    }

    throw new Response("Failed to run AI assessment. Please try again.", {
      status: 500,
    });
  }
}

export async function getLatestAssessment(id: string | undefined): Promise<ClaimAssessmentResponse | null> {
  if (!id) {
    throw new Response("Claim ID is missing.", { status: 400 });
  }

  const parsedId = parseInt(id, 10);
    if (isNaN(parsedId)) {
      throw new Response("Invalid Claim ID.", { status: 400 });
  }

  try {
    const response = await apiClient.get(
      `/claims/${parsedId}/ai-assessment/latest`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
        },
    });
    return response.data;
  } catch(error) {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError<ApiErrorResponse>;
      throw new Response(
        axiosError.response?.data?.errorMessage ??
        axiosError.message ??
        "Failed to get the latest assessment. Please try again.",
        {status: axiosError.status ?? 500},
      );
    }

    throw new Response("Failed to get the latest assessment. Please try again.", {
      status: 500,
    });
  }
}
