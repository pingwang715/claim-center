import React from "react";
import OnlineStatImage from "../assets/util/OnlineStat.svg";
import PageHeading from "./PageHeading";

export default function Home(): React.JSX.Element {
  return (
    <div className="flex flex-col min-h-[810px] bg-normalbg">
      <div className="py-4 bg-normalbg font-primary">
        <div className="max-w-4xl mx-auto px-4 text-center">
          <PageHeading title="Welcome to Claim Center!">
            Please login to continue.
          </PageHeading>
          <div className="text-center text-secondary flex flex-col items-center mt-24">
            <img
              src={OnlineStatImage}
              alt="Online Stat"
              className="w-full max-w-[576px] mx-auto"
            />
          </div>
        </div>
      </div>
    </div>
  );
}
