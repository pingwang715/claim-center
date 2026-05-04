import React, { useState, useMemo } from "react";
import type { Claim } from "../types/claims";
import SearchBox from "./SearchBox";
import Dropdown from "./Dropdown";
import { useAuth } from "../store/auth-context";
import { Link }from "react-router-dom";

interface Props {
  claims: Claim[];
}

const STATUS_STYLES: Record<string, string> = {
  SUBMITTED: "bg-blue-100 text-blue-800",
  UNDER_REVIEW: "bg-amber-100 text-amber-800",
  APPROVED: "bg-green-100 text-green-800",
  REJECTED: "bg-red-100 text-red-800",
  OVERRIDDEN_APPROVED: "bg-green-100 text-green-800",
  OVERRIDDEN_REJECTED: "bg-red-100 text-red-800",
};

const STATUS_LABELS: Record<string, string> = {
  SUBMITTED: "Submitted",
  UNDER_REVIEW: "Under review",
  APPROVED: "Approved",
  REJECTED: "Rejected",
  OVERRIDDEN_APPROVED: "Overridden approved",
  OVERRIDDEN_REJECTED: "Overridden rejected",
};

const sortList: string[] = [
  "All",
  "Submitted",
  "Under review",
  "Approved",
  "Rejected",
  "Overridden approved",
  "Overridden rejected",
];

export const ClaimsTable: React.FC<Props> = ({ claims }) => {
  const { user } = useAuth();
  const [searchText, setSearchText] = useState("");
  const [selectedSort, setSelectedSort] = useState("All");

  const filteredAndSortedClaims = useMemo(() => {
    if (!Array.isArray(claims)) {
      return [];
    }

    const filteredClaims = claims.filter((claim) =>
      claim.title.toLowerCase().includes(searchText.toLowerCase()),
    );

    switch (selectedSort) {
      case "All":
        return filteredClaims;
      case "Submitted":
        return filteredClaims.filter((claim) => claim.status === "SUBMITTED");
      case "Under review":
        return filteredClaims.filter(
          (claim) => claim.status === "UNDER_REVIEW",
        );
      case "Approved":
        return filteredClaims.filter((claim) => claim.status === "APPROVED");
      case "Rejected":
        return filteredClaims.filter((claim) => claim.status === "REJECTED");
      case "Overridden approved":
        return filteredClaims.filter(
          (claim) => claim.status === "OVERRIDDEN_APPROVED",
        );
      case "Overridden rejected":
        return filteredClaims.filter(
          (claim) => claim.status === "OVERRIDDEN_REJECTED",
        );

    }
  }, [claims, searchText, selectedSort]);

  function handleSearchChange(inputSearch: string) {
    setSearchText(inputSearch);
  }

  function handleSortChange(sortType: string) {
    setSelectedSort(sortType);
  }

  return (
    <div className="min-w-[580px] mt-8">
      {user?.role === "ROLE_CLAIMANT" ? (<div className="border border-primary bg-primary p-5 rounded-xl shadow-md mb-4 hover:bg-dark">
        <Link to="/create" className="text-white">
          Create a claim
        </Link>
      </div>) : (<div></div>)}
      <div className="bg-gray-50 border border-gray-200 rounded-xl p-5 shadow-md">
        <h2 className="text-xs font-medium text-gray-400 uppercase tracking-wider mb-4">
          My claims
        </h2>
        <div className="flex flex-col sm:flex-row justify-between items-center gap-4 pt-4 pb-4">
          <SearchBox
            label="Search"
            placeholder="Search claims..."
            value={searchText}
            handleSearch={(value) => handleSearchChange(value)}
          />
          <Dropdown
            label="Sort by"
            options={sortList}
            selectedValue={selectedSort}
            handleSort={(value) => handleSortChange(value)}
          />
        </div>
        <ul className="divide-y divide-gray-200">
          {filteredAndSortedClaims.length > 0 ? (
            filteredAndSortedClaims.map((claim) => (
              <li key={claim.id} className="flex items-center gap-3 py-3">
                <div className="flex-1 min-w-0 text-left">
                  <p className="text-sm text-gray-900 truncate">
                    {claim.title}
                  </p>
                </div>

                <span
                  className={`text-xs font-medium px-2.5 py-0.5 rounded-full shrink-0 ${STATUS_STYLES[claim.status]}`}
                >
                  {STATUS_LABELS[claim.status]}
                </span>
              </li>
            ))
          ) : (
            <p className="text-sm text-gray-400 text-center py-6">
              No claims found
            </p>
          )}
        </ul>
      </div>
    </div>
  );
};
