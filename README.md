# todo
Mock up project for onboarding asked by Joe



# 요구사항

## 요약

- To-do ist 앱 만들기

## 기능범위

**(사용자 입장에서)**

1. Todo 작성기능
    - User는 Todo 작업을 등록할수 있어야한다
        - Due date, 내용1, 내용2(디테일)
2. 예약 todo 기능
    - User는 todo아이템을 특정 시간에 등록되게 할 수 있어야한다.
        - @POST /todos param: 내용~, date_at
        - *스케쥴러가 필요하다.*
    - 
3. Todo 리스트 가져오기
4. Todo 리스트 업데이트하기

**(시스템 입장에서)**

1. 시스템은 Due date가 1시간전인 Todo아이템을 찾아서 
    1. 이메일을 보내준다
    2. 슬랙을 보내준다
    3. 문자를 보내준다
2. 이메일, 문자, 슬랙 보낸기록을 Kafka에 다 보내서 저장해놓고, 이후에 분석목적으로 컨슘해서 redis에 업로드하기
    
    msg:{
    
    event_type: EMAIL_SENT | SMS_SENT | SLACK_SENT
    
    내용대충
    
    }
    
    저장되는 형태는 daily형태로 몇개 보냈늕
    
    key: email_sent:{날짜}
    
    value:
    
    1. 얼마나 많은 메세지가 갔는지
    2. 평균 텍스트 길이가 얼마나되는지
3. 하루에 한번씩 데일리 리포트 보내줌 to admin
    - admin이 보는내용
        - 지난날 얼마나 많은 메세지가 갔는지
        - 평균 텍스트 길이가 어떻게 되는지

# 외부 API 연동

- 이메일 연동: email SaaS찾아서
- 슬랙연동
- 문자연동: 문자보내는 SaaS찾아서
