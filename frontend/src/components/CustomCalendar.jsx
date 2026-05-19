import React, { useRef, useState, useEffect } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import "/src/css/CustomCalendar.css";

const CustomCalendar = ({ events, onEventClick, onDateClick }) => {
  const calendarRef = useRef(null);
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);

  useEffect(() => {
    const calendarApi = calendarRef.current.getApi();
    const currentDate = calendarApi.getDate();
    setYear(currentDate.getFullYear());
    setMonth(currentDate.getMonth() + 1);
  }, []);

  const handleYearChange = (e) => {
    const newYear = parseInt(e.target.value, 10);
    setYear(newYear);
    moveToDate(newYear, month);
  };

  const handleMonthChange = (e) => {
    const newMonth = parseInt(e.target.value, 10);
    setMonth(newMonth);
    moveToDate(year, newMonth);
  };

  const moveToDate = (year, month) => {
    const calendarApi = calendarRef.current.getApi();
    calendarApi.gotoDate(`${year}-${String(month).padStart(2, "0")}-01`);
  };

  const currentYear = new Date().getFullYear(); // 현재 연도
  const startYear = currentYear - 5; // 과거 5년
  const endYear = currentYear + 1; // 미래 1년


  return (
    <div>
      <div className="yearselect">
      <select value={year} onChange={handleYearChange} className="yearvalue">
          {/* 연도 선택 범위를 현재 연도 기준으로 과거 5년 ~ 미래 1년으로 제한 */}
          {Array.from({ length: endYear - startYear + 1 }, (_, i) => {
            const optionYear = startYear + i; // 시작 연도에서 인덱스를 더하여 생성
            return (
              <option key={optionYear} value={optionYear}>
                {optionYear}년
              </option>
            );
          })}
        </select>
        <select value={month} onChange={handleMonthChange} className="monthvalue">
          {Array.from({ length: 12 }, (_, i) => (
            <option key={i + 1} value={i + 1}>
              {i + 1}월
            </option>
          ))}
        </select>
      </div>

      <FullCalendar
        ref={calendarRef}
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        headerToolbar={{
          left: "prev,next today",
          center: "title",
          right: "dayGridMonth,timeGridWeek,timeGridDay",
        }}
        events={events}
        eventClick={onEventClick}
        dateClick={onDateClick}
        height="700px"
      />
    </div>
  );
};

export default CustomCalendar;
