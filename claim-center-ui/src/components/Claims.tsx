import React from "react";
import { useAuth } from "../store/auth-context";
import { StatCard } from "./StatCard";
import { ClaimsTable } from "./ClaimsTable";
import type { Claim } from "../types/claims";

export default function Claims(): React.JSX.Element {
  const { user }= useAuth();
  const MOCK_CLAIMS : Claim[] = [
    {
      id: 1,
      title: "Water damage in the kitchen",
      status: "approved",
    },
    {
      id: 2,
      title: "Vehicle rear-end collision",
      status: "under_review",
    },
  ]

  return (

    <div className="min-h-[852px] bg-normalbg py-4 px-2 font-primary">
      <div className="max-w-[852px] mx-auto flex flex-col items-center justify-center text-center">
        <div className="mb-2">
          <h1 className="text-2xl font-primary font-extrabold text-primary py-2">
            {`Welcome back, ${user?.firstName} ☺️`}
          </h1>
          <p className="text-secondary text-sm">Here's an update on your claims</p>
        </div>
        <div className="grid grid-cols-2 gap-3 mt-4">
          <StatCard label="Active claims" value={2} />
          <StatCard label="Pending payout" value="€200" variant="warning"/>
        </div>

        <ClaimsTable claims={MOCK_CLAIMS}></ClaimsTable>
      </div>
    </div>
  )
}
