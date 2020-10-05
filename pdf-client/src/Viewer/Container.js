import React, { useEffect, useMemo, useState } from "react";
import pdfjs from "pdfjs-dist";

import { useFetchPDFs } from "../utils";
import { service } from "../service";
import { Row, Col, Divider } from "antd";
import { Viewer } from "./Viewer";
import { SubnailsList } from "./Subnails/Subnails";

export const ViewContainer = () => {
  const [pdfs, setPdfs] = useState([]);
  const [pdfProxises, setPdfProxies] = useState([]);

  useFetchPDFs(setPdfs);

  useEffect(() => {
    const fetch = async () => {
      const downloadPdf = id =>
        service
          .getById(id)
          .then(buffer => pdfjs.getDocument({ data: buffer }).promise);
      const pdfProxises = pdfs.map(({ id }) => ({
        id,
        promise: downloadPdf(id)
      }));

      setPdfProxies(pdfProxises);
    };
    fetch();
  }, [pdfs]);

  const list = useMemo(
    () =>
      pdfProxises
        .map(({ id, promise }, index) =>
          Array.from({ length: pdfs[index].pages }).map((_, index) => ({
            promise,
            page: index + 1
          }))
        )
        .reduce((acc, item) => [...acc, ...item], []),
    [pdfProxises]
  );
  const subNailsArray = useMemo(()=>pdfs.map((item, index) => ({
    ...item,
    proxy: pdfProxises[index]
  })), [pdfProxises])

  const [page, setPage] = useState(null)
  return (
    <>
      {!!pdfProxises.length && (
        <div className={"layout-container"}>
            <SubnailsList
              pdfs={subNailsArray}
              scrollTo={setPage}
            />
          <Viewer list={list} scrollToPage={page} />
         </div>)}
    </>
  );
};
