import React from "react";
import { BrowserRouter as Router, Routes, Route,useLocation } from "react-router-dom";
import { HelmetProvider } from "react-helmet-async";
import "./App.css";
import AdminHeader from "./components/AdminHeader";
import AdminSideBar from "./components/AdminSideBar";
import Dashboard from "./pages/Dashboard";
import UsersList from "./pages/admin/UsersList";
import CourseList from "./pages/admin/CourseList";
import SessionList from "./pages/admin/SessionList";
import SessionDetail from "./pages/admin/SessionDetail";
import AttList from "./pages/admin/AttList"
import AttListDetail from "./pages/admin/AttListDetail";
import AttLog from "./pages/admin/AttLog";
import PartList from "./pages/admin/PartList"
import TrainList from "./pages/admin/TrainList";
import TestList from "./pages/admin/TestLIst";
import TestSubmit from "./pages/admin/TestSubmit";


import ManagerHeader from "./components/ManagerHeader";
import ManagerSideBar from "./components/ManagerSideBar";
import ManagerDashboard from "./pages/manager/ManagerDashboard";

import ManagerUserList from "./pages/manager/ManagerUsersList";
import ManagerCourseList from "./pages/manager/ManagerCourseLIst";
import ManagerSessionList from "./pages/manager/ManagerSessionList";
import ManagerSessionDetail from "./pages/manager/ManagerSessionDetail";
import ManagerAttList from "./pages/manager/ManagerAttList"
import ManagerAttListDetail from "./pages/manager/ManagerAttListDetail";
import ManagerAttLog from "./pages/manager/ManagerAttLog";
import ManagerPartList from "./pages/manager/ManagerPartList"
import ManagerTrainList from "./pages/manager/ManagerTrainList";
import ManagerTestList from "./pages/manager/ManagerTestLIst";
import ManagerTestSubmit from "./pages/manager/ManagerTestSubmit";

import InstrHeader from "./components/InstrHeader";
import InstrSideBar from "./components/InstrSideBar";
import InstrDashboard from "./pages/instr/InstrDashboard";
import InstrSessionList from "./pages/instr/InstrSessionList"
import InstrSessionDetail from "./pages/instr/InstrSessionDetail";
import InstrPartList from "./pages/instr/InstrPartList"
import InstrTestList from "./pages/instr/InstrTestLIst";
import InstrTestSubmit from "./pages/instr/InstrTestSubmit";

import StudentHeader from "./components/StudentHeader";
import StudentSidebar from "./components/StudentSidebar";

import StudentDashboard from "./pages/student/StudentDashboard";
import StudentSessionList from "./pages/student/StudentSessionList";
import StudentAtt from "./pages/student/StudentAtt";

function Layout() {
  const location = useLocation();

  const isStudent = location.pathname.startsWith("/student");

  const getHeader = () => {
    if (location.pathname.startsWith("/admin")) return <AdminHeader />;
    if (location.pathname.startsWith("/manager")) return <ManagerHeader />;
    if (location.pathname.startsWith("/instr")) return <InstrHeader />;
    if (isStudent) return <StudentHeader />;
    return null; // 기본 헤더
  };

  const getSidebar = () => {
    if (location.pathname.startsWith("/admin")) return <AdminSideBar />;
    if (location.pathname.startsWith("/manager")) return <ManagerSideBar />;
    if (location.pathname.startsWith("/instr")) return <InstrSideBar />;
    if (isStudent) return <StudentSidebar />;
    return null; // 기본 사이드바
  };

  return (
    
    <div className={`app-container ${isStudent ? "student" : ""}`}>
      {getHeader()}
      <div className="content-container">
        {getSidebar()}
        <div className="main-content">
          <Routes>
            {/* Admin Routes */}
            <Route path="/admin/dashboard" element={<Dashboard />} />

            <Route path="/admin/users/list" element={<UsersList />} />

            <Route path="/admin/KDT/list" element={<CourseList />} />
            <Route path="/admin/KDT/course/:courseId" element={<SessionList />} />
            <Route path="/admin/KDT/session/:sessionId" element={<SessionDetail />} />

            <Route path="/admin/KDT/:kdtSessionId/att/list" element={<AttList />} />
            <Route path="/admin/KDT/:kdtSessionId/att/detail/:kdtPartId" element={<AttListDetail />} />
            <Route path="/admin/KDT/:kdtSessionId/att/log/:kdtPartId" element={<AttLog />} />

            <Route path="/admin/KDT/:kdtSessionId/part/list" element={<PartList />} />

            <Route path="/admin/KDT/:kdtSessionId/train/list" element={<TrainList />} />

            <Route path="/admin/KDT/:kdtSessionId/test/list" element={<TestList />} />
            <Route path="/admin/KDT/:kdtSessionId/test/submit/:kdtTestId/list" element={<TestSubmit />} />
            {/* Manager Routes */}  
            <Route path="/manager/dashboard" element={<ManagerDashboard />} />

            <Route path="/manager/users/list" element={<ManagerUserList />} />

            <Route path="/manager/KDT/list" element={<ManagerCourseList />} />
            <Route path="/manager/KDT/course/:courseId" element={<ManagerSessionList />} />
            <Route path="/manager/KDT/session/:sessionId" element={<ManagerSessionDetail />} />

            <Route path="/manager/KDT/:kdtSessionId/att/list" element={<ManagerAttList />} />
            <Route path="/manager/KDT/:kdtSessionId/att/detail/:kdtPartId" element={<ManagerAttListDetail />} />
            <Route path="/manager/KDT/:kdtSessionId/att/log/:kdtPartId" element={<ManagerAttLog />} />
         
            <Route path="/manager/KDT/:kdtSessionId/part/list" element={<ManagerPartList />} />

            <Route path="/manager/KDT/:kdtSessionId/train/list" element={<ManagerTrainList />} />
            
            <Route path="/manager/KDT/:kdtSessionId/test/list" element={<ManagerTestList />} />
            <Route path="/manager/KDT/:kdtSessionId/test/submit/:kdtTestId/list" element={<ManagerTestSubmit />} />
            {/* Instructor Routes */}
            <Route path="/instr/dashboard" element={<InstrDashboard />} />
            <Route path="/instr/KDT/list" element={<InstrSessionList />} />
            <Route path="/instr/KDT/session/:sessionId" element={<InstrSessionDetail />} />

            <Route path="/instr/KDT/:kdtSessionId/part/list" element={<InstrPartList />} />

            <Route path="/instr/KDT/:kdtSessionId/test/list" element={<InstrTestList />} />
            <Route path="/instr/KDT/:kdtSessionId/test/submit/:kdtTestId/list" element={<InstrTestSubmit />} />

            {/* Student Routes */}
            <Route path="/student/dashboard" element={<StudentDashboard />} />
            <Route path="/student/KDT/list" element={<StudentSessionList />} />
            <Route path="/student/KDT/:kdtSessionId/att/detail" element={<StudentAtt />} />

          </Routes>
        </div>
      </div>
    </div>
  );
}

function App() {
  return (
    <HelmetProvider>
      <Router basename="/view">
        <Layout />
      </Router>
    </HelmetProvider>
  );
}

export default App;