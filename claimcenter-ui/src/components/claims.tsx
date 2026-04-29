import React from "react";
import { useAuth } from "../store/auth-context";
import { StatCard } from "./StatCard";

export default function Claims(): React.JSX.Element {
  const { user }= useAuth();

  return (

    <div className="min-h-[852px] bg-normalbg py-4 px-2 font-primary">
      <div className="max-w-[820px] mx-auto flex flex-col items-center justify-center text-center">
        <div className="mb-2">
          <h1 className="text-2xl font-primary font-extrabold text-center text-primary mt-4 py-2">
            {`Welcome back, ${user?.firstName} ☺️`}
          </h1>
          <p className="text-secondary text-sm">Here's an update on your claims</p>
        </div>
        <div className="grid grid-cols-3 gap-3">
          <StatCard label="Active claims" value={2} />
          <StatCard label="Pending payout" value="€200" variant="warning"/>
          <StatCard label="Last update" value="2 days ago" small />
        </div>

      </div>
    </div>
  )
}
