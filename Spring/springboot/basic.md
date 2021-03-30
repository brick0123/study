# 스프링 부트 설정을 정리한 파일입니다.
### 로깅
로깅 퍼사드 VS 로거
- Commons Logging, SLF4j
- JUL, Log4J2, Logback

SLF4j를 통해 기본적으로 로거는 Logback을 통해서 로그를 찍는다.
로깅 설정
- 로그 출력: logging.file, logging.path
- 로그 레벨 조정: logging.level.패키지 = 로그 레벨

Logback Config - logback-spring.xml, logback-spring.groovy

``` xml
<configuration>
  <include resource="org/springframework/boot/logging/logback/base.xml"/>
  <logger name="com.woodcock" level="DEBUG"/>
</configuration>
```