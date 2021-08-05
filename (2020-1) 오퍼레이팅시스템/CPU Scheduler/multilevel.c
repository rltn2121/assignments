#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#define CAPACITY 10 // maximum size of ready queue
#define Q_CNT 5     // number of queues
enum ReadyQueueType { FCFS = 0, SJF, RR, PRIORITY };
sem_t mutex;
int context_switch_cnt = -1; // context switch count
int lastest_pid = 0;         // Pid of the most recently processed process
typedef struct {
    int class_num;
    int pid;
    int priority;
    int burst_time;
} Process;

typedef struct {
    int sched_type;  // scheduling type(0: FCFS, 1: SJF, 2: RR, 3: PRIORITY)
    Process p[CAPACITY]; 
    int front;       // index of the first process in the queue
    int rear;        // index of the first process in the queue
    int count; 
    int total_burst_time;
    int time_quantum;
} Ready_queue;


void setReadyQueueType(Ready_queue*);                   // initialize queues
void insertProcess(Ready_queue*);                       // input processes
void printProcess(Ready_queue*);                        // print processes
int compareWithBurstTime(const void*, const void*);     // Sort processes in ascending order by burst time
int compareWithPriority(const void*, const void*);      // Sort processes in ascending order by priority
int compareWithTotalBurstTime(const void*, const void*);// Sort queues in ascending order by total burst time
void* sched_FCFS(void*);                                // do FCFS scheduling
void* sched_SJF(void*);                                 // do SJF scheduling
void* sched_PRIORITY(void*);                            // do Priority scheduling
void* sched_RR(void*);                                  // do Round robin scheduling
void* sched_Queue(void* q);                             // do scheduling between queues

int main(int argc, char* argv[]) {
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
}
// initialize queues
void setReadyQueueType(Ready_queue* mlq) {
    for (int i = 0; i < Q_CNT; i++) {
        mlq[i].front = -1;
        mlq[i].rear = -1;
        mlq[i].count = 0;
        mlq[i].total_burst_time = 0;
        mlq[i].time_quantum = 0;
    }
    // Determine the queue scheduling method
    mlq[0].sched_type = FCFS;
    mlq[1].sched_type = SJF;
    mlq[2].sched_type = RR;
    mlq[3].sched_type = PRIORITY;
    mlq[4].sched_type = RR;

    // Round robin scheduling
    mlq[2].time_quantum = 3;
    mlq[4].time_quantum = 15;
}
// input processes
void insertProcess(Ready_queue* mlq) {
    // input number of processes
    int num_of_process;
    scanf("%d", &num_of_process);
    while (num_of_process--) {
        int class, pid, priority, burst_time;
        scanf("%d %d %d %d", &class, &pid, &priority, &burst_time);
        // if queue is full
        while (mlq[class].count == CAPACITY) {
            printf("[Queue %d] is full! Use another Queue\n", class);
            scanf("%d %d %d %d", &class, &pid, &priority, &burst_time);
        }

        mlq[class].rear = (mlq[class].rear + 1) % CAPACITY;
        int index = mlq[class].rear;
        mlq[class].p[index].class_num = class;
        mlq[class].p[index].pid = pid;
        mlq[class].p[index].priority = priority;
        mlq[class].p[index].burst_time = burst_time;

        mlq[class].count++;
        mlq[class].total_burst_time += burst_time;
    }
}
// print processes
void printProcess(Ready_queue* mlq) {
    for (int i = 0; i < Q_CNT; i++) {
        for (int j = 0; j < mlq[j].count; j++)
            printf("class: %d, pid: %d, priority: %d, burst_time: %d\n", mlq[i].p[j].class_num, mlq[i].p[j].pid, mlq[i].p[j].priority, mlq[i].p[j].burst_time);
    }
}
// Sort processes in ascending order by burst time
int compareWithBurstTime(const void* a, const void* b) {
    Process* p1 = (Process*)a;
    Process* p2 = (Process*)b;
    return p1->burst_time > p2->burst_time;
}
// Sort processes in ascending order by priority
int compareWithPriority(const void* a, const void* b) {
    Process* p1 = (Process*)a;
    Process* p2 = (Process*)b;
    return p1->priority > p2->priority;
}
// Sort queues in ascending order by total burst time
int compareWithTotalBurstTime(const void* a, const void* b) {
    Ready_queue* r1 = (Ready_queue*)a;
    Ready_queue* r2 = (Ready_queue*)b;
    return r1->total_burst_time > r2->total_burst_time;
}
// do FCFS scheduling
void* sched_FCFS(void* q) {
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
}
// do SJF scheduling
void* sched_SJF(void* q) {
    printf("------ sched_SJF() ------\n");
    Ready_queue* rq = (Ready_queue*)q;
    int current_idx = 0;    // current index in ready queue
    int running_time = 0;   // running time in ready queue
    // Sort processes in ascending order by burst time
    qsort(rq, rq->count, sizeof(rq->p[0]), compareWithBurstTime);
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
}
// do Priority scheduling 
void* sched_PRIORITY(void* q) {
    printf("------ sched_PRIORITY() ------\n");
    Ready_queue* rq = (Ready_queue*)q;
    // Sort processes in ascending order by priority
    qsort(rq, rq->count, sizeof(rq->p[0]), compareWithPriority);
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
}
// do Round robin scheduling
void* sched_RR(void* q) {
    printf("------ sched_RR() ------\n");
    Ready_queue* rq = (Ready_queue*)q;
    int current_time = 0;
    int current_idx = 0;    // current index in ready queue
    int running_time = 0;   // running time in ready queue
    int p_cnt = rq->count;
    while (running_time < rq->total_burst_time) {
        // if the current process has been processed for a time quantum,
        if (current_time == rq->time_quantum) {
            current_time = 0;
            current_idx = (current_idx + 1) % p_cnt;
            printf("\n");
        }
        // if the current process is not finished
        if (rq->p[current_idx].burst_time > 0) {
            printf("%d ", rq->p[current_idx].pid);
            rq->p[current_idx].burst_time--;
            current_time++;
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
            current_idx = (current_idx + 1) % p_cnt;
            if (current_time != 0) {
                current_time = 0;
                printf("\n");
            }
        }
    }
    printf("\ncontext switch: %d\n", context_switch_cnt);
    pthread_exit(0);
}
// do scheduling between queues
void* sched_Queue(void* mlq) {
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
}