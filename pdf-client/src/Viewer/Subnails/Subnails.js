import React, { useEffect, useState, memo, useCallback } from "react";
import { Skeleton } from "antd";

import AutoSizer from "react-virtualized-auto-sizer";
import { FixedSizeList as List, areEqual } from "react-window";
import memoize from "memoize-one";
import { splitEvery, path } from "ramda";
import uid from "uid";
import "./Subnails.scss";

const width = 120;
const height = Math.round(width * 1.414);

const renderSubnail = proxyPage => {
  let canvas = document.createElement("canvas");
  canvas.width = width;
  canvas.height = height;
  const context = canvas.getContext("2d");
  var viewport = proxyPage.getViewport({ scale: 1 });
  var scale = width / viewport.width;
  var scaledViewport = proxyPage.getViewport({ scale: scale });

  var renderContext = {
    canvasContext: context,
    viewport: scaledViewport
  };
  return {
    render: proxyPage.render(renderContext),
    canvas: canvas
  };
};

let counter = 0
const resetCounter = ()=> {
  counter  = 0
}
const updateCounter = () => {
  counter = counter + 1 
}

function generateSubnails(document, cache) {
  return splitEvery(
    2,
    Array.from({ length: document.pages }).map((_, index) => {
      const cacheId = uid(10);
      updateCounter()
      return {
        cacheId,
        renderSubnail: async () => {
          const PDFdocument = await document.proxy.promise;
          const page = await PDFdocument.getPage(index + 1);
          const { render, canvas } = renderSubnail(page);
          return {
            promise: render.promise
              .then(() =>
                new Promise(resolve => canvas.toBlob(resolve)).then(
                  URL.createObjectURL
                )
              )
              .then(blob => {
                cache[cacheId] = blob;
                return blob;
              })
              .catch(() => null),
            cancel: () => {
              render._internalRenderTask.running &&
                render._internalRenderTask.cancel();
            }
          };
        },
        page: index + 1,
        index: counter
      };
    })
  );
}

const PageSubnail = ({ getRender, id, cache, page, index, scrollTo }) => {
  const src = cache[id];
  const [ignore, setScr] = useState(false);
  useEffect(() => {
    let cancelFunc;
    if (!src) {
      window.requestAnimationFrame(() =>
        getRender()
          .then(({ promise, cancel }) => {
            cancelFunc = cancel;

            return promise;
          })
          .then(item => {
            setScr(true);
          })
      );
    }

    return () => {
      cancelFunc && cancelFunc();
    };
  }, [src]);

  const scroll = useCallback(() => scrollTo(index), [index]);

  return (
    <div
      onClick={scroll}
      className={"subnail-item"}
    >
      <div      style={{ maxWidth: width, minHeight: height, backgroundColor: 'white', width: '100%' }}
>

      {src ? <img src={src} /> : <Skeleton className={"skeleton"} active />}

</div>
      <div className="page-number">{page}</div>
    </div>
  );
};

const Row = memo(({ index, style, data, scrollTo }) => {
  const first = path(["items", index, 0], data);
  const second = path(["items", index, 1], data);
  const cache = data.cache;
  return (
    <div style={style} key={index}>
      <div className="item">
        <div className="contianer">
          {first && (
            <PageSubnail
              id={first.cacheId}
              cache={cache}
              page={first.page}
              index={(first.index)}
              scrollTo={scrollTo}
              getRender={first.renderSubnail}
            />
          )}
          {second && (
            <PageSubnail
              id={second.cacheId}
              cache={cache}
              page={second.page}
              index={second.index}
              scrollTo={scrollTo}
              getRender={second.renderSubnail}
            />
          )}
        </div>
      </div>
    </div>
  );
}, areEqual);

const createItemData = memoize((pdfs, cache) => ({
  cache,
  items: pdfs
    .map(doc => generateSubnails(doc, cache))
    .reduce((acc, item) => [...acc, ...item], [])
}));

export const SubnailsList = memo(({ pdfs, scrollTo }) => {
  const cache = {};
  resetCounter()
  const data = createItemData(pdfs, cache);
  return (
    <div className="subnails">
      <AutoSizer>
        {({ height: containerHeight, width }) => (
          <List
            className="subnails-list"
            itemCount={data.items.length}
            itemSize={height + 30}
            itemData={data}
            width={width}
            height={containerHeight}
          >
            {props => <Row {...props} scrollTo={scrollTo} />}
          </List>
        )}
      </AutoSizer>
    </div>
  );
});
