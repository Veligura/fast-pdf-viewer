import React, { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import { Spin } from "antd";
import { Link } from "react-router-dom";

import { Button } from "antd";
import { service } from "../service";
import "./UploadPDF.scss";

export function UploadPDF() {
  const [loading, setLoading] = useState(false);
  const onDrop = useCallback(acceptedFiles => {
    setLoading(true);
    acceptedFiles
      .map(file => {
        const formData = new FormData();
        formData.append("file", file);
        return () => service.upload(formData);
      })
      .reduce((acc, item) => acc.then(item), Promise.resolve())
      .then(() => setLoading(false));
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: "application/pdf"
  });

  return (
    <div className={"UploadPDF"}>
      <Spin spinning={loading} tip="Loading...">
        <div className={"uploadInput"} {...getRootProps()}>
          <input {...getInputProps()} />
          <p>Drag 'n' drop some files here, or click to select files</p>
        </div>
      </Spin>

      <Link to="/">
        <Button>Back</Button>
      </Link>
    </div>
  );
}
