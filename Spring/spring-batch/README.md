### 배치 애플리케이션이란?
단발성 대용량의 데이터를 처리하는 애플리케이션. 배치 어플리케이션은 다음의 조건을 만족해야한다.
- 대용량: **대량의 데이터**를 가져오거나, 계산하는 등의 처리가 가능해야 한다.
- 자동화:  사용자의 개입 없이 **자동**으로 실행.
- 견고성: **충돌/중단 없이** 진행되어야 한다.
- 신뢰성: 로깅 알림을 통해 **이슈를 추적**.
- 성능: **독립적인 수행**과 **퍼포먼스**가 보장되어야한다.

### Reader & Writer
|Data|기술|설명|
|---|---|---|
|Datbase|jdbc|페이징, 커서 업데이트 등|
|Datbase|Hiberbate|페이징 커서
|Datbase|JPA|페이징 사용 가능
|File|Flat file|지정한 구분자로 파싱 지원
|File|XML|XML 파싱 지원
<<<<<<< HEAD

</br>

### 스프링 배치 기본 구조 & 처리 흐름도

![구성](../assets/spring-batch/batch-1.png)
[reference](https://terasoluna-batch.github.io/guideline/5.0.0.RELEASE/en/Ch02_SpringBatchArchitecture.html)

![구성](../assets/spring-batch/batch-2.png)
[reference](https://terasoluna-batch.github.io/guideline/5.0.0.RELEASE/en/Ch02_SpringBatchArchitecture.html)

### Job
- 일련의 프로세스를 요약하는 단일 실행 단위.

``` java
@Bean
public Job simpleJob() {
    return jobBuilderFactory.get("Job Name")
        .start(step())
        .build();
}
```

### Step
- Job을 구성하는 처리 단위. 하나의 Job에 여러 Step이 포함될 수 있다. 조건부 수행, 병렬화 등 가능.

``` java
@Bean Step stepBean() {
    return stepBuilderFactory.get("simpleStep1")
    .tasklet((contribution, chunkContext) -> {
        log.info(">>> This is Step1 <<<");
         return RepeatStatus.FINISHED;
    })
    .build();
}
```

## Meta-Data Schema


![meta](../assets/spring-batch/batch-3.png)
[reference](https://docs.spring.io/spring-batch/docs/current/reference/html/schema-appendix.html)


### BATCH_JOB_INSTANCE
- JobInstance와 관련된 정보를 담고있다. 전체 계층 구조의 최상위 역할을 한다.

같은 Batch Job이라도 Job Paramter가 다르면 `BATCH_JOB_INSTANCE`에는 기록된다. Job PAramter가 성공한 이력이 있을 경우 똑같은 값은 실행되지 않는다. `JobInstanceAlreadyCompleteException` 발생.

``` java
@Bean
public Job simpleJob() {
    return jobBuilderFactory.get("simpleJob") 
        .start(simpleStep(null))
        .build();
}

@Bean
@JobScope
public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
return stepBuilderFactory.get("simpleStep1")
    .tasklet((contribution, chunkContext) -> {
        log.info(">>> This is Step <<<");
        log.info(">>> requestDate = {}", requestDate);
        return RepeatStatus.FINISHED;
    })
    .build();
}
```

</br>

### BATCH_JOB_INSTANCE
![meta](../assets/spring-batch/batch-4.png)

- 동일한 파라미터 실행시 `JobInstanceAlreadyCompleteException` 발생
  
![error](../assets/spring-batch/batch-5.png)

### BATCH_JOB_EXECUTION
`JobExecution` 객체와 관련된 모든 정보를 보유한다. Job이 실행될 때마다 항상 새 JobExecution과 새 행이 있다.

### BATCH_JOB_EXECUTION_PARAMS

`JobParamters` 객체와 관련된 모든 정보를 보유한다.

![params](../assets/spring-batch/batch-6.png)
