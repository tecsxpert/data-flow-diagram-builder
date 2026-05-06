import { useState } from "react";
import fileAPI from "../services/fileApi";

function FileUpload() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");

  const handleUpload = async () => {
    if (!file) {
      setMessage("Please select a file");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      await fileAPI.post("/files/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      setMessage("Upload successful");
    } catch (err) {
      setMessage("Upload failed");
    }
  };

  return (
    <div className="p-5">
      <h2 className="text-lg font-bold mb-3">Upload File</h2>

      <input
        type="file"
        onChange={(e) => setFile(e.target.files[0])}
      />

      <button
        onClick={handleUpload}
        className="bg-blue-500 text-white px-4 py-2 ml-2"
      >
        Upload
      </button>

      {message && <p className="mt-2">{message}</p>}
    </div>
  );
}

export default FileUpload;