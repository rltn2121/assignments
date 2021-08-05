# CPU Scheduler Simulation
<h2>오퍼레이팅시스템-001분반 12161567 박기수</h2>

<h3>1.	모델 설계</h3>

본 모델은 Multilevel queue scheduling 방식을 사용했다. 큐 간 스케줄링 방식은 SJF scheduling에 착안하여 각 큐의 burst time의 합이 최소인 큐 먼저 처리한다. 각 큐의 waiting time을 최소화할 수 있을 것으로 예상된다. 큐의 개수는 총 5개(#define Q_CNT 5)이며, 각 큐는 최대 10개(#define CAPACITY 10)의 프로세스를 저장할 수 있다. Multilevel queue를 구성하는 첫 번째 큐의 scheduling 방식은 FCFS scheduling, 두 번째 큐는 SJF scheduling, 세 번째 큐는 Round Robin scheduling(time quantum = 3), 네 번째 큐는 Priority scheduling, 다섯 번째 큐는 Round Robin scheduling(time quantum = 15)를 사용했다.

 **Scheduling 방식**

<img src="https://user-images.githubusercontent.com/54628612/84037717-ee588f00-a9d9-11ea-9253-65b47bb45a20.png"></img>

<h3>2.	구현</h3>

본 모델에서 사용한 전역 변수는 총 3개이다. 각 쓰레드의 동기화를 위한 semaphore 변수(mutex)와 가장 마지막으로 처리된 프로세스의 식별자를 저장하는 lastest_pid, context switch 횟수를 저장하는 context_switch_cnt로 구성되며, lastest_pid와 context_switch_cnt는 각 쓰레드의 critical section에서 처리된다.
프로세스와 각 큐들을 효과적으로 처리하기 위해서 Process 구조체와 Ready_queue 구조체를 선언했다. Process 구조체는 process가 어떤 큐에서 처리될 지(class_num), 프로세스의 식별자(pid), 프로세스의 우선순위(priority), 프로세스의 버스트 시간(burst_time)을 저장한다. Ready_queue 구조체는 circular queue로 구현했으며, Queue의 첫 번째 원소와 마지막 원소를 가리키는 front, rear 변수를 가진다. 또한, 저장된 프로세스의 개수(count), 저장된 프로세스의 burst time의 합(total_burst_time), 해당 큐의 scheduling 방식(sched_type), Round robin 방식을 사용할 때 필요한 time_quantum 변수, 각 프로세스를 저장하는 큐(Process p[])로 구성된다. 

 **Process 구조체**
 
 <pre><code> typedef struct {
    int class_num;
    int pid;
    int priority;
    int burst_time;
} Process; </code></pre>
 
**Ready_queue 구조체**

  <pre><code>typedef struct {
    int sched_type;  // scheduling type(0: FCFS, 1: SJF, 2: RR, 3: PRIORITY)
    Process p[CAPACITY]; 
    int front;       // index of the first process in the queue
    int rear;        // index of the first process in the queue
    int count; 
    int total_burst_time;
    int time_quantum;
} Ready_queue; </code></pre>

setReadyQueueType() 함수는 각 큐를 초기화하고 scheduling 방식을 결정한다. Round Robin 방식을 사용할 경우 time_quantum을 0이 아닌 값으로 설정한다. insertProcess() 함수는 처리할 프로세스의 수와 프로세스의 정보(class, pid, priority, burst_time)를 입력한다. 만약 삽입하려는 큐가 가득 찼을 경우 오류 메시지를 출력하고 프로세스 정보를 다시 입력한다. printProcess() 함수는 입력된 프로세스의 정보를 출력한다. compareWithBurstTime(), compareWithPriority() 함수는 프로세스를 각각 burst time, priority 기준 오름차순 정렬하고, compareWithTotalBurstTime() 함수는 각 Ready queue를 total burst time 기준 오름차순 정렬한다. sched_FCFS(), sched_SJF(), sched_Priority(), sched_RR() 함수들은 각각 FCFS, SJF, Priority, Round robin scheduling을 실행한다. sched_Queue() 함수는 각 큐의 scheduling을 실행하는 쓰레드를 실행시킨다.

**사용한 함수 목록**

<img src="https://user-images.githubusercontent.com/54628612/84037721-f0225280-a9d9-11ea-8187-2a21c0f7f5ef.JPG"></img>

<h3>3.	실행</h3>

main 함수에서는 multilevel queue를 생성하고 프로세스들을 입력한다. 프로세스를 모두 입력하면 입력한 프로세스를 출력하고 큐 간 scheduling을 수행하는 쓰레드를 실행한다. 쓰레드가 종료될 때까지 기다렸다가 쓰레드가 종료되면 semaphore 변수를 제거하고 프로그램을 종료한다.

**main()**

<pre><code>int main(int argc, char* argv[]) {
    Ready_queue mlq[Q_CNT];
    setReadyQueueType(mlq);
    insertProcess(mlq);
    printProcess(mlq);

    sem_init(&mutex, 0, 1);
    pthread_t tid;
    // Run a thread that schedules between queues
    pthread_create(&tid, NULL, sched_Queue, (void*)mlq);
    pthread_join(tid, NULL);
    sem_destroy(&mutex);
    return 0;
}</code></pre>

큐 간 scheduling을 수행하는 sched_queue() 함수는 stdlib.h의 qsort() 함수를 호출하여 각 큐를 total_burst_time 순서로 정렬한다. 이후 for문에서 각 큐를 scheduling하는 쓰레드를 실행하는데, 각 큐의 scheduling type에 맞는 함수를 호출한다. 하나의 쓰레드가 실행하면 해당 쓰레드가 종료될 때까지 기다린다. 
 
**sched_Queue()**

<pre><code>void* sched_Queue(void* mlq) {
    Ready_queue* rq = (Ready_queue*)mlq;
    // Sort queues in ascending order by total burst time
    qsort(rq, Q_CNT, sizeof(rq[0]), compareWithTotalBurstTime);

    pthread_t tid[Q_CNT];
    // execute threads matching the queue's scheduling type
    for (int i = 0; i < Q_CNT; i++) {
        if (rq[i].sched_type == FCFS)
            pthread_create(&tid[i], NULL, sched_FCFS, (void*)&rq[i]);
        else if (rq[i].sched_type == SJF)
            pthread_create(&tid[i], NULL, sched_SJF, (void*)&rq[i]);
        else if (rq[i].sched_type == RR)
            pthread_create(&tid[i], NULL, sched_RR, (void*)&rq[i]);
        else if (rq[i].sched_type == PRIORITY)
            pthread_create(&tid[i], NULL, sched_PRIORITY, (void*)&rq[i]);
        pthread_join(tid[i], NULL);
    }
}</code></pre>

sched_FCFS() 함수는 현재 인덱스를 저장하는 current_idx, 큐의 실행시간을 저장하는 running_time 변수를 가진다. FCFS scheduling은 프로세스가 입력된 순서대로 처리하므로, 별도의 정렬을 하지 않는다. while 문을 total_burst_time만큼 반복 실행한다. 만약 current_idx의 프로세스의 burst time이 0보다 클 경우 해당 프로세스의 pid를 출력하고 burst time을 1만큼 감소시키고 running time을 1만큼 증가시킨다. 그 후에 critical section으로 진입하여 현재 처리중인 프로세스와 마지막으로 실행한 프로세스가 다를 경우, context_switch_cnt를 1만큼 증가시킨다. lastest_pid를 갱신한다. 만약 current_idx 프로세스의 burst time이 0일 경우 해당 프로세스의 처리가 완료되었음을 의미하므로 다음 프로세스를 수행하기 위해 current_idx를 1만큼 증가시키고 count를 1만큼 감소시킨다. 또한 front를 1만큼 증가시켜 다음 프로세스를 가리킨다. while문의 수행이 끝나면 마지막으로 count를 1만큼 감소시키고, front를 1만큼 증가시킨다. 모든 프로세스가 처리됐으므로 front와 rear는 같은 곳을 가리킨다.

**sched_FCFS()**

<pre><code>void* sched_FCFS(void* q) {
    printf("------ sched_FCFS() ------\n");
    Ready_queue* rq = (Ready_queue*)q;
    int current_idx = 0;    // current index in ready queue
    int running_time = 0;   // running time in ready queue
    
    while (running_time < rq->total_burst_time) {
        // if the current process is not finished
        if (rq->p[current_idx].burst_time > 0) {
            printf("%d ", rq->p[current_idx].pid);
            rq->p[current_idx].burst_time--;
            running_time++;
            sem_wait(&mutex);
            // start critical section
            // if the last processed process is different from the currently processed process
            if (lastest_pid != rq->p[current_idx].pid)
                context_switch_cnt++;
            lastest_pid = rq->p[current_idx].pid;
            // end critical section
            sem_post(&mutex);
        }
        // if the current process is finished
        else {
            current_idx++;
            rq->count--;
            rq->front = (rq->front + 1) % CAPACITY;
            printf("\n");
        }
    }
    rq->count--;
    rq->front = (rq->front + 1) % CAPACITY;
    printf("\ncontext switch: %d\n", context_switch_cnt);
    pthread_exit(0);
}</code></pre>

큐 간 scheduling을 수행하는 sched_queue() 함수는 stdlib.h의 qsort() 함수를 호출하여 각 큐를 total_burst_time 순서로 정렬한다. 이후 for문에서 각 큐를 scheduling하는 쓰레드를 실행하는데, 각 큐의 scheduling type에 맞는 함수를 호출한다. 하나의 쓰레드가 실행하면 해당 쓰레드가 종료될 때까지 기다린다. 
 
**sched_Queue()**

<pre><code>void* sched_Queue(void* mlq) {
    Ready_queue* rq = (Ready_queue*)mlq;
    // Sort queues in ascending order by total burst time
    qsort(rq, Q_CNT, sizeof(rq[0]), compareWithTotalBurstTime);

    pthread_t tid[Q_CNT];
    // execute threads matching the queue's scheduling type
    for (int i = 0; i < Q_CNT; i++) {
        if (rq[i].sched_type == FCFS)
            pthread_create(&tid[i], NULL, sched_FCFS, (void*)&rq[i]);
        else if (rq[i].sched_type == SJF)
            pthread_create(&tid[i], NULL, sched_SJF, (void*)&rq[i]);
        else if (rq[i].sched_type == RR)
            pthread_create(&tid[i], NULL, sched_RR, (void*)&rq[i]);
        else if (rq[i].sched_type == PRIORITY)
            pthread_create(&tid[i], NULL, sched_PRIORITY, (void*)&rq[i]);
        pthread_join(tid[i], NULL);
    }
}</code></pre>

sched_SJF(), sched_Priority() 함수는 sched_FCFS() 함수의 while문 앞에 각각 burst time, priority 순으로 정렬시키는 작업을 추가로 수행해주고, 나머지 부분은 sched_FCFS()와 같다.
sched_RR() 함수는 time quantum을 고려해야 하므로, current_time 변수를 추가했다. while문을 total_burst_time만큼 반복 실행한다. 만약 현재 프로세스를 time_quantum만큼 처리했을 경우 current_time을 0으로 초기화하고 current_idx를 1만큼 증가시킨다. 여기서 주의할 점은 프로세스를 번갈아 가면서 처리해야 하므로 circular queue에서 index를 증가시키는 것처럼 current_idx를 증가시킨 후 프로세스 개수와 나머지 연산을 한다. Burst time에 따른 프로세스 처리는 다른 scheduling 방식과 동일하며 current_time 처리를 추가적으로 한다. 만약 burst time이 0인데 current_time이 0이 아닐 경우 current_time을 0으로 변경한다.

<h3>4.	성능 분석</h3>

위의 Gantt chart는 큐를 순서대로 처리한 결과이고, 아래의 Gantt chart는 큐를 본 모델의 방식대로 처리한 결과이다. 첫번째 Gantt chart의 waiting time은 0+41+78+149+187 = 455인데, 두번째 Gantt chart의 waiting time은 0+37+75+116+187 = 415로, 본 모델의 scheduling 방식의 waiting time이 더 짧다. 

**Scheduling 방식 별 Gantt chart**

  <img src="https://user-images.githubusercontent.com/54628612/84037724-f0bae900-a9d9-11ea-8d11-8bf80ba9f4e0.png"></img>

다음은 Round robin scheduling에서 time quantum에 따른 context switch 횟수 차이를 비교할 것이다. 3번째 큐에 삽입된 프로세스들의 burst time과 5번째 큐에 삽입된 프로세스들의 burst time은 같지만 time quantum은 각각 3, 15로 다르다. Context switch 횟수는 각각 18, 5로서, time quantum이 3일때의 context switch 횟수가 더 작다. 

**time quantum에 따른 context switch의 차이**

 <img src="https://user-images.githubusercontent.com/54628612/84037723-f0bae900-a9d9-11ea-93c3-1e3e7ca35216.JPG"></img>
 
<h3>5.	결론</h3>

대부분의 multilevel queue의 큐 간 스케줄링 방식은 고정 우선순위의 선점형 스케줄링이지만, 본 모델에서는 큐의 total burst time이 가장 작은 큐 먼저 처리했다. 본 모델의 스케줄링 방식을 따르면 각 큐의 waiting time을 줄일 수 있다. 하지만 우선순위가 가장 높은 큐의 total burst time이 가장 크다면 해당 큐는 가장 마지막에 처리될 것이다. 따라서 우선순위와 waiting time간의 trade-off가 발생한다. 또한, Round robin 방식에서 time quantum이 짧을 수록 context switch가 더 많이 발생한다. 만약 context switch의 overhead가 클 경우 전체적인 실행 시간이 느려 질 것이다.
