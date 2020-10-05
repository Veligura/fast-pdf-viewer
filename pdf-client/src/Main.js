import React, { useState } from "react";
import { Button } from "antd";
import { Link } from "react-router-dom";
import { formatBytes, useFetchPDFs } from "./utils";
import "./Main.scss";

const PDFItem = ({ item }) => (
  <div>
    {item.fileName} - {formatBytes(item.size)}, pages: {item.pages}
  </div>
);

export const Main = () => {
  const [pdfs, setPdfs] = useState([]);
  useFetchPDFs(setPdfs);
  return (
    <div className={"main"}>
      <div className="list">
        {!!pdfs.length
          ? pdfs.map(item => <PDFItem item={item} />)
          : "PDFs list is empty. Please upload a few one"}
      </div>
      <div className="buttons">
        <Link to="/view">
          <Button type="primary">View PDFs</Button>
        </Link>
        <Link to="/upload">
          <Button type="dashed" >Upload PDFs</Button>
        </Link>
      </div>
    </div>
  );
};
