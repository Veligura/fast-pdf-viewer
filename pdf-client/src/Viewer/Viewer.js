import React, { useState, useEffect, useRef, memo } from "react";
import { Page } from "react-pdf/dist/entry.webpack";

import { FixedSizeList as List, areEqual } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";

import "./Viewer.scss";

export const PageWrapper = ({ pdf: PDFPromise, pageNumber, height = 297 }) => {
  const [pdf, setPdf] = useState(null);
  useEffect(() => {
    if (PDFPromise) {
      PDFPromise.then(pdf => setPdf(pdf));
    }
  }, [PDFPromise]);
  return pdf ? (
    <Page
      height={height}
      pdf={pdf}
      loading={() => <div className="no-data" />}
      pageNumber={pageNumber}
    />
  ) : (
    <div className="no-data" style={{ width: height * 0.7, height }} />
  );
};

export const Viewer = ({ list, scrollToPage }) => {
  const listRef = useRef()
  useEffect(()=> {
    if(listRef.current && scrollToPage){
      console.log(scrollToPage)
      listRef.current.scrollToItem(scrollToPage-1)
    }
  }, [scrollToPage])



  return (
    <div className="Viewer">
      <AutoSizer>
        {({ height, width }) => (
          <List
            ref={listRef}
            className="List"
            itemCount={list.length}
            itemSize={800}
            width={width}
            height={height}
          >
            {memo(({ index, style, ...rest }) => {
              const listItem = list[index];
              return (
                <div style={style} key={index}>
                  <div className="list-item">
                    <PageWrapper
                      height={800 - 20}
                      pdf={listItem.promise}
                      pageNumber={listItem.page}
                    />
                  </div>
                </div>
              );
            }, areEqual )}
          </List>
        )}
      </AutoSizer>
    </div>
  );
};
