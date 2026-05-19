import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Chart, Filler } from 'chart.js';
import 'bootstrap/dist/css/bootstrap.min.css';
import React from "react";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
);
Chart.register(Filler);

function InstrDashboard() {
  return (
    
        <div className="container mt-4">
          <h1>Dashboard</h1>
         
          <div className="row mt-4">
            <div className="col-md-6">
             
            </div>
            <div className="col-md-6">
            
            </div>
          </div>
          <div className="mt-4">
            <h2>Data Table</h2>
           
          </div> 
        </div>
      
  );
}

export default InstrDashboard;
