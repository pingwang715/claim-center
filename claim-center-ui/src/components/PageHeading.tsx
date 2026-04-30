import React from "react";
import PageTitle from "./PageTitle";

interface PageHeadingProps {
  title: string;
  children: React.ReactNode;
}

export default function PageHeading({ title, children} : PageHeadingProps): React.JSX.Element {

  return (
    <div>
      <PageTitle title={title} />
      <p className="font-primary leading-6 text-secondary text-center">{children}</p>
    </div>
  )
}
