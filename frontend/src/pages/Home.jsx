import { useEffect } from "react";
import api from "../services/api";

function Home() {
  useEffect(() => {
    api.get("/all")
      .then(res => console.log(res.data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold">Dashboard</h1>
    </div>
  );
}

export default Home;
