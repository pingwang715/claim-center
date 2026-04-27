import React from "react";
import PageTitle from "./PageTitle";
import { Link } from "react-router-dom";

export default function Login(): React.JSX.Element {
  const lableStyle: string = "block text-lg font-semibold text-primary mb-2";
  const textFieldStyle: string =
    "w-full px-4 py-2 text-base border rounded-md transition border-primary focus:ring focus:ring-dark focus:outline-none text-secondary bg-white placeholder-gray-400";

  return (
    <div className="min-h-[820px] flex items-center justify-center font-primary bg-normalbg">
      <div className="bg-white shadow-md rounded-lg max-w-md w-full px-8 py-6">
        <PageTitle title="Login" />
        <form className="space-y-6">
          <div>
            <label htmlFor="username" className={lableStyle}>
              Username
            </label>
            <input
              type="text"
              id="username"
              name="username"
              placeholder="Your Username"
              required
              className={textFieldStyle}
            />
          </div>

          <div>
            <label htmlFor="password" className={lableStyle}>
              Password
            </label>
            <input
              type="text"
              id="password"
              name="password"
              placeholder="Your Password"
              required
              minLength={4}
              maxLength={20}
              className={textFieldStyle}
            />
          </div>

          <div>
            <button type="submit" className="w-full px-6 py-2 text-white text-xl rounded-md transition duration-200 bg-primary hover:bg-dark">
              Login
            </button>
          </div>
        </form>

        <p>
          Don't have an account?{" "}
          <Link to="/register" className="text-primary hover:text-dark transition duration-200">
            Register Here
          </Link>
        </p>
      </div>
    </div>
  );
}
