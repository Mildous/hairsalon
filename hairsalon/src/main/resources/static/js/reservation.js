$(document).ready(function() {
    $.datepicker.setDefaults($.datepicker.regional['ko']);
    $("#rsvDate").datepicker({
        changeMonth: true,
        changeYear: true,
        nextText: '다음 달',
        prevText: '이전 달',
        dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
        dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
        monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        dateFormat: 'yy-mm-dd',
        minDate: 0 // ( 0 : 오늘 이전 날짜 선택 불가)
    });
    if (errorMessage != null) {
        alert(errorMessage);
    }
    $('#typeId').on('change', function () {
        $('#longTime').val(($("#typeId option:selected").data('minute')));
        changeDate($("#rsvDate").val());
    });
});

function changeDate(rsvDate) {
    // 날짜 변경 시 기존 버튼 삭제
    var timeList = document.getElementById("timeList");
    // 예약 시작 시간과 종료 시간 초기화
    $("#rsvStartTime").val("");
    $("#rsvEndTime").val("");
    while (timeList.firstChild) {
        timeList.removeChild(timeList.firstChild);
    }
    var today = moment().startOf('day');
    var selectedDate = moment(rsvDate);
    if (selectedDate.isBefore(today)) {
        var errorMessage = moment(selectedDate).format("YYYY년 MM월 DD일") + "에는 예약이 불가능합니다.";
        $('#timeList').html('<div class="alert alert-danger">' + errorMessage + '</div>');
    } else {
        var token = $("meta[name='_csrf']").attr("content");
        var header =  $("meta[name='_csrf_header']").attr("content");
        var paramData = {
            rsvDate: rsvDate
        };
        var param = JSON.stringify(paramData);

        $.ajax({
            url: "/reserve/getTime",
            type: "POST",
            contentType: "application/json",
            data: param,
            beforeSend: function (xhr){
                /* 데이터 전송 전 헤더에 csrf 값 설정 */
                xhr.setRequestHeader(header, token);
            },
            dataType: "json",
            cache: false,
            success: function (data) {
                var availableTimes = getAvailableTimes(data, $("#typeId option:selected").data('minute'));

                // typeId의 값이 선택되지 않았을 경우 버튼을 출력하지 않음
                if($('#longTime').val() != "") {
                    if (availableTimes.length === 0) {
                        // 예약 가능한 시간이 없을 경우 메세지 출력
                        var errorMessage = moment(selectedDate).format("YYYY년 MM월 DD일") + "에는 예약이 불가능합니다.";
                        $('#timeList').html('<div class="alert alert-danger">' + errorMessage + '</div>');
                    } else {
                        // 예약 가능한 시간대 목록을 이용해 예약 가능한 버튼을 생성하는 코드 작성
                        for (var i = 0; i < availableTimes.length; i++) {
                            createButton(availableTimes[i]);
                        }
                    }
                }
            },
            error: function () {
                console.log("error");
            }
        });
    }
}

function getAvailableTimes(reservedTimes, takeMinutes) {
    var availableTimes = [];
    var startTime = moment('11:00', 'HH:mm');
    var endTime = moment('20:00', 'HH:mm');
    var reservedRanges = reservedTimes.map(function (time) {
        var start = moment(time.split('-')[0], 'HH:mm');
        var end = moment(time.split('-')[1], 'HH:mm');
        return moment.range(start, end);
    });
    while (startTime.isBefore(endTime)) {
        var endTimeCandidate = moment(startTime).add(takeMinutes, 'minutes');
        var timeRange = moment.range(startTime, endTimeCandidate);
        var isOverlap = false;
        reservedRanges.forEach(function(reservedRange) {
            if (reservedRange.overlaps(timeRange)) isOverlap = true;
        });
        if (!isOverlap)
            availableTimes.push(
                startTime.format('HH:mm') + '-' + endTimeCandidate.format('HH:mm')
            );
        startTime.add(30, 'minutes');
    }
    return availableTimes;
}

// 버튼 생성 함수
function createButton(time) {
    var button = document.createElement("button");
    button.innerHTML = time;
    button.value = time + ":00"; // 버튼 값 추가
    button.type = "button"; // type 속성 추가
    button.classList.add("btn", "btn-success", "mx-1", "my-1"); // 클래스 추가
    // 버튼 클릭 이벤트 등록
    button.addEventListener("click", function() {
        // 클릭한 버튼의 시간대에 대한 처리
        selectTime(this.value);
    });
    // 버튼을 표시할 요소에 추가
    document.getElementById("timeList").appendChild(button);
}

// 예약 시간 버튼 선택 시
function selectTime(time) {
    // 선택한 버튼의 값(예: "11:00-11:30")을 시작 시간과 종료 시간으로 분리
    var start = time.split("-")[0];
    var end = time.split("-")[1];

    // 시작 시간과 종료 시간을 HH:mm 형식으로 포맷
    var formattedStart = moment(start, "HH:mm").format("HH:mm");
    var formattedEnd = moment(end, "HH:mm").format("HH:mm");

    // 시작 시간과 종료 시간을 input 요소에 설정
    $("input[name=rsvStartTime]").val(formattedStart);
    $("input[name=rsvEndTime]").val(formattedEnd);
}