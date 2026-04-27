import React from "react";
import { Link, NavLink } from "react-router-dom";
import ClaimCenterIcon from "../assets/util/ClaimCenter.svg";

export default function Header(): React.JSX.Element {
  const label: string = "Login";
  const href: string = "/login";

  const navLinkClass: string = "text-center text-lg font-primary font-semibold text-primary py-2";

  return (
    <header className="border-b border-gray-300 sticky top-0 z-20 bg-normalbg">
      <div className="flex items-center justify-between mx-auto max-w-[1152px] px-6 py-4">
        <div className="flex">
          <Link to="/" className="flex items-center gap-2 text-lg font-primary font-semibold text-primary py-2">
            <img src={ClaimCenterIcon} alt="Claim Center Icon" className="h-8 w-8"/>
            <span className="font-bold">Claim Center</span>
          </Link>
        </div>

        <nav className="flex items-center py-2 z-10">
          <ul>
            <li>
              <NavLink to={href} className={navLinkClass}>
                {label}
              </NavLink>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  )
}
