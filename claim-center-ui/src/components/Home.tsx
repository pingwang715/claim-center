import React from "react";
import OnlineStatImage from "../assets/util/OnlineStat.svg";
import PageHeading from "./PageHeading";
import { useAuth } from "../store/auth-context";
import Onboardingclaimant from "../assets/util/OnboardingClaimant.svg";
import DataTableManager from "../assets/util/DataTableManager.svg";
import ProcessAdjuster from "../assets/util/ProcessAdjuster.svg";

export default function Home(): React.JSX.Element {
  const { isAuthenticated, user } = useAuth();
  const role = user?.role;

  const renderAuthenticatedContent = () => {
    switch (role) {
      case "ROLE_CLAIMANT":
        return (
          <div>
            <PageHeading title="Welcome to your personal Claim Center!">
              Create a claim, follow the status, get your payment.
            </PageHeading>
            <div className="text-center text-secondary flex flex-col items-center mt-24">
                <img
                  src={Onboardingclaimant}
                  alt="onboarding"
                  className="w-full max-w-[576px] mx-auto"
                />
            </div>
          </div>
        );

        case "ROLE_ADJUSTER":
          return (
            <div>
              <PageHeading title="Welcome to your working space - Claim Center!">
                Approve or reject - you help clients get what they deserve.
              </PageHeading>
              <div className="text-center text-secondary flex flex-col items-center mt-16">
                <img
                  src={ProcessAdjuster}
                  alt="process"
                  className="w-full max-w-[576px] mx-auto"
                />
              </div>
            </div>
          );

        case "ROLE_MANAGER":
          return (
            <div>
              <PageHeading title="Welcome to your working space - Claim Center!">
                Follow the claims - you help your team and clients for better.
              </PageHeading>
              <div className="text-center text-secondary flex flex-col items-center mt-24">
                <img
                  src={DataTableManager}
                  alt="process"
                  className="w-full max-w-[576px] mx-auto"
                />
              </div>
            </div>
          );

      default:
        return null;
    }
  }

  return (
    <div className="flex flex-col min-h-[810px] bg-normalbg">
      <div className="py-4 bg-normalbg font-primary">
        <div className="max-w-4xl mx-auto px-4 text-center">
          {isAuthenticated ? (
            <div>{renderAuthenticatedContent()}
            </div>
          ) : (
          <div>
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
          )}
        </div>
      </div>
    </div>
  )
}
