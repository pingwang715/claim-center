import React from "react";
import { useAuth } from "../store/auth-context";
import { ClaimsTable } from "./ClaimsTable";
import type { Claim } from "../types/claims";
import apiClient from "../api/apiClient";
import { useLoaderData } from "react-router-dom";
import axios, { AxiosError } from "axios";

interface ApiErrorResponse {
  errorMessage?: string;
}

export default function Claims(): React.JSX.Element {
  const { user } = useAuth();
  const claims = useLoaderData();

  return (
    <div className="min-h-[852px] bg-normalbg py-4 px-2 font-primary">
      <div className="min-w-[852px] mx-auto flex flex-col items-center justify-center text-center">
        <div className="mb-2">
          <h1 className="text-2xl font-primary font-extrabold text-primary py-2">
            {`Welcome back, ${user?.firstName} ☺️`}
          </h1>
          <p className="text-secondary text-sm">
            Here's an update on your claims
          </p>
        </div>

        <ClaimsTable claims={claims}></ClaimsTable>
      </div>
    </div>
  );
}

export async function claimsLoader(): Promise<Claim[]> {
  try {
    const response = await apiClient.get<Claim[]>("/claims", {
      headers: { Authorization: `Bearer ${localStorage.getItem("jwtToken")}` },
    });
    console.log("full resonse:", JSON.stringify(response.data, null, 2));
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
}
