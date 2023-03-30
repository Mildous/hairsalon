$(document).ready(function() {
    if(errorMessage != null){
        alert(errorMessage);
    }
    bindDomEvent();
});

function bindDomEvent(){
    $(".custom-file-input").on("change", function() {
        var fileName = $(this).val().split("\\").pop();  //이미지 파일명
        var fileExt = fileName.substring(fileName.lastIndexOf(".")+1); // 확장자 추출
        fileExt = fileExt.toLowerCase(); //소문자 변환

        if(fileExt != "jpg" && fileExt != "jpeg" && fileExt != "gif" && fileExt != "png" && fileExt != "bmp") {
            alert("이미지 파일만 등록이 가능합니다.");
            return;
        }

        $(this).siblings(".custom-file-label").html(fileName);
    });
}

function saveReview(reserveId) {
    // 이전에 출력되어 있던 에러 메시지를 초기화
    $('p.fieldError').text('');
    var formData = new FormData(document.getElementById('reviewForm'));

    $.ajax({
        url: '/review/new/' + reserveId,
        type: "POST",
        enctype: 'multipart/form-data',
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
            $('#reviewModal').modal('hide');
            location.reload();
        },
        error: function (xhr, status, error) {
            var response = xhr.responseJSON;
            console.log(response);

            if (typeof response === 'undefined') {
                alert('이미지1은 필수입니다.');
            } else if (typeof response === 'string') {
                alert(response.toString());
            } else {
                // 검증 결과 정보를 화면에 출력
                response.forEach(function (error) {
                    var fieldName = error.field;
                    var errorMessage = error.defaultMessage;
                    // 각 필드에 해당하는 에러 메시지 출력
                    $('p.fieldError[data-field="' + fieldName + '"]').text(errorMessage);
                });
            }
        }
    });
}

function updateReview(reviewId) {
    // 이전에 출력되어 있던 에러 메시지를 초기화
    $('p.fieldError').text('');
    var formData = new FormData(document.getElementById('reviewForm'));

    $.ajax({
        url: '/review/edit/' + reviewId,
        type: "POST",
        enctype: 'multipart/form-data',
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        },
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
            alert('정상적으로 수정되었습니다.');
            $('#reviewModal').modal('hide');
            location.reload();
        },
        error: function (xhr, status, error) {
            var response = xhr.responseJSON;
            console.log(response);

            if (typeof response === 'undefined') {
                alert('이미지1은 필수입니다.');
            } else if (typeof response === 'string') {
                alert(response.toString());
            } else {
                // 검증 결과 정보를 화면에 출력
                response.forEach(function (error) {
                    var fieldName = error.field;
                    var errorMessage = error.defaultMessage;
                    // 각 필드에 해당하는 에러 메시지 출력
                    $('p.fieldError[data-field="' + fieldName + '"]').text(errorMessage);
                });
            }
        }
    });
}

function deleteReview(reviewId) {
    if (confirm("정말 삭제하시겠습니까?")) {
        $.ajax({
            type: "POST",
            url: "/review/delete/" + reviewId,
            beforeSend: function (xhr) {
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
            },
            success: function (response) {
                alert(response);
                $('#reviewModal').modal('hide');
                // 리뷰가 삭제되었으므로 화면에서도 삭제
                $("#"+reviewId).remove();
                location.reload();
            },
            error: function (xhr, status, error) {
                alert(xhr.responseText);
            }
        });
    }
}