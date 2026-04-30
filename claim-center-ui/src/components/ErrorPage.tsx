import React from "react";
import Header from "./Header";
import Footer from "./Footer";
import PageTitle from "./PageTitle";
import errorImage from "../assets/util/Error.svg";
import { Link } from "react-router-dom";
import { useRouteError } from "react-router-dom";

export default function ErrorPage(): React.JSX.Element {
  const routeError = useRouteError() as { status: number; statusText: string; data: string } | null;
  let errorTitle: string = "Oops! Something went wrong";
  let errorMessage: string =
    "An expected error occurred. Please try again later.";
  if (routeError) {
    errorTitle = routeError.statusText;
    errorMessage = routeError.data;
  }

  return (
    <div className="flex flex-col">
      <Header />
      <main className="flex-grow">
        <div className="min-h-[820px] py-12 bg-normalbg font-primary">
          <div className="max-w-4xl mx-auto px-4">
            <PageTitle title={errorTitle} />
          </div>
          <div className="text-center text-gray-600 flex flex-col items-center mb-16">
            <p className="max-w-[576px] px-2 mx-auto leading-6 mb-8">
              {errorMessage}
            </p>
            <img
              src={errorImage}
              alt="Error"
              className="w-full max-w-[576px] mx-auto mb-8"
            />
            <Link
              to="/home"
              className="py-3 px-6 text-white text-xl rounded-md transition duration-200 bg-primary hover:bg-dark font-semibold mb-2"
            >
              Back to Home
            </Link>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
