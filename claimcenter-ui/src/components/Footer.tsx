import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHeart } from "@fortawesome/free-solid-svg-icons";

export default function Footer(): React.JSX.Element {

  return (
    <footer className="flex justify-center items-center py-4 text-gray-700 bg-normalbg border-t border-gray-300">
      Built with
      <FontAwesomeIcon
        icon={faHeart}
        className="text-red-600 mx-1"
        aria-hidden="true"
      />
      in Munich
      <a href="mailto:pingwang715@gmail.com" className="px-6 hover:text-amber-800 hover:underline">
        Contact me
      </a>
    </footer>
  );
}
