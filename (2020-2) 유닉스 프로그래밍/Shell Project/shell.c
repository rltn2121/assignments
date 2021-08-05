#include <string.h>
#include <signal.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <setjmp.h>
#include <stdlib.h>
#define MAX_CMD_ARG 15
#define BUFSIZ 256

const char *prompt = "myshell> ";
char* cmdvector[MAX_CMD_ARG];
char* pipevector[MAX_CMD_ARG][MAX_CMD_ARG];
char* temp[MAX_CMD_ARG - 1];
char  cmdline[BUFSIZ];
static sigjmp_buf jmpbuf;
void fatal(char *str){
    perror(str);
      exit(1);
}

static void sig_chdir(int signo){
    if(signo == SIGUSR1){
        if(chdir(cmdvector[1])==-1)
                perror("main()");
    }
}
static void sig_goto_start(int signo){
    if(signo == SIGINT || signo == SIGQUIT){
        printf("\n");
        siglongjmp(jmpbuf, 1);
    }
}
static void sig_ignore(int signo){
    if(signo == SIGINT || signo == SIGQUIT){
        printf("\n");
    }
}
static void sig_chld(int signo){
    if(signo == SIGCHLD){
        waitpid(-1, NULL, WNOHANG);
    }
}

void redirection_read(char** cmdvector, int* arg_cnt){
    int i, j, fd;
    for(i = 0; i<*arg_cnt; i++){
        if(cmdvector[i] == NULL)
            return;
        if(strcmp(cmdvector[i], "<") == 0)
            break;
    }

   if(i == *arg_cnt)
        return;
   

    if(i == *arg_cnt -1)
        fatal("syntax error");

    if((fd = open(cmdvector[i + 1], O_RDONLY)) == -1)
        fatal(cmdvector[i + 1]);
    
    dup2(fd, 0);
    close(fd);
    for(j = i; j<*arg_cnt; j++){
        if(cmdvector[j+1] == NULL)
            break;
        cmdvector[j] = cmdvector[j + 1];
    }
    cmdvector[j] = NULL;
    *arg_cnt--;
}

void redirection_write(char** cmdvector, int* arg_cnt){
    int i, j, fd;
    for(i = 0; i<*arg_cnt; i++){
        if(cmdvector[i] == NULL)
            return;
        if(strcmp(cmdvector[i], ">") == 0)
            break;
    }

    if(i == *arg_cnt)
        return;

    if(i == *arg_cnt -1)
        fatal("syntax error");

    if((fd = open(cmdvector[i + 1], O_RDWR | O_CREAT | O_TRUNC, 0644)) == -1)
        fatal(cmdvector[i + 1]);
    
    dup2(fd, 1);
    close(fd);
    for(j = i; j<*arg_cnt; j++){
        if(cmdvector[j+2] == NULL)
            break;
        cmdvector[j] = cmdvector[j + 2];
    }
    cmdvector[j] = NULL;
    cmdvector[j+1] = NULL;
    *arg_cnt -= 2;
}

int chk_pipe(char** cmdvector, int arg_cnt){
    //printf("chk_pipe start\n");
    if( strcmp(cmdvector[0], "|")== 0 || strcmp(cmdvector[arg_cnt-1], "|") == 0)
        fatal("pipe");

    for(int i = 0; i<arg_cnt; i++){
        if(strcmp(cmdvector[i], "|") == 0)
            return i;
    }
   // printf("chk_pipe end\n");
    return 0;
}

void execute_pipe(char** cmdvector){

  //  printf("pipe chk1\n");
    
    int i=0, j=0, k = 0;
    int pipe_cnt = 0;
    int pipe_idx = 0;
    int pid, pid1, pid2;
    int p[MAX_CMD_ARG][2];
    int pipevector_size_list[MAX_CMD_ARG];

    while(cmdvector[i] != NULL){
        if(strcmp(cmdvector[i], "|") == 0){
            pipevector[pipe_cnt][pipe_idx] = NULL;
            pipevector_size_list[pipe_cnt] = pipe_idx;
            pipe_cnt++;
            i++;
            pipe_idx = 0;
            continue;
        }
        pipevector[pipe_cnt][pipe_idx++] = cmdvector[i++];
    }
    //printf("pipe_cnt: %d\n", pipe_cnt);
   // for(i = 0; i<pipe_cnt; i++)
        
    // for(int i = 0; i<=pipe_cnt; i++){
    //     for(int j = 0; pipevector[i][j] != NULL; j++)
    //     //    printf("pipevector[%d][%d] = %s\n", i, j, pipevector[i][j]);

    //   //  printf("------\n");
    // }

    for(i = 0; i<pipe_cnt; i++){
        pipe(p[i]);
        switch(pid = fork()){
            case -1:
                fatal("execute_pipe()");
            case 0:
                redirection_read(pipevector[i], &pipevector_size_list[i]);
                dup2(p[i][1], 1);
                close(p[i][1]);
                close(p[i][0]);

                if( i > 0){
                    dup2(p[i-1][0], 0);
                    close(p[i-1][0]);
                }

                execvp(pipevector[i][0], pipevector[i]);
                fatal("execute_pipe() - fork() case 0");
            default:
                dup2(p[i][0], 0);
                close(p[i][0]);
                close(p[i][1]);
                if(i == pipe_cnt - 1){
                    redirection_write(pipevector[pipe_cnt], &pipevector_size_list[pipe_cnt]);

                    execvp(pipevector[pipe_cnt][0], pipevector[pipe_cnt]);
                    fatal("execute_pipe() - fork() default");
                }
        }

    }

    
    // switch(pid1 = fork()){
    //     case -1:
    //         fatal("execute_pipe()");
    //     case 0:
    //         redirection_read(pipevector1, &i);
    //         dup2(p[1], 1);
    //         close(p[1]);
    //         close(p[0]);

    //         execvp(pipevector1[0], pipevector1);
    //         fatal("execute_pipe() - fork() case 0");
    //     default:
    //         if(chk_pipe(pipevector2, k)){
                
    //         }
    //         else{
    //             redirection_write(pipevector2, &k);
    //             dup2(p[0], 0);
    //             close(p[0]);
    //             close(p[1]);

    //             execvp(pipevector2[0], pipevector2);
    //             fatal("execute_pipe() - fork() default");
    //         }
    // }
         
   // printf("pipe end\n");

}
int makelist(char *s, const char *delimiters, char** list, int MAX_LIST){   
    int i = 0;
    int numtokens = 0;
    char *snew = NULL;

    if( (s==NULL) || (delimiters==NULL) ) return -1;

    snew = s + strspn(s, delimiters);   /* Skip delimiters */
    if( (list[numtokens]=strtok(snew, delimiters)) == NULL )
        return numtokens;
    
    numtokens = 1;
    
    while(1){   
      if( (list[numtokens]=strtok(NULL, delimiters)) == NULL)
          break;
      if(numtokens == (MAX_LIST-1)) return -1;
      numtokens++;
    }
    return numtokens;
}

int main(int argc, char**argv) {
    //printf("hi\n");
    int i=0;
    pid_t pid;
    signal(SIGUSR1, sig_chdir);
    signal(SIGINT, sig_goto_start);
    signal(SIGQUIT, sig_goto_start);
    while (1) {
        
        sigsetjmp(jmpbuf, 1);
       
        fputs(prompt, stdout);
        fgets(cmdline, BUFSIZ, stdin);
        
        cmdline[strlen(cmdline) -1] = '\0';
        
        int arg_cnt=0;
        int status = 0;
        int redirection_val = 0;
        arg_cnt = makelist(cmdline, " \t", cmdvector, MAX_CMD_ARG);
        switch(pid=fork()){
            case 0:
                signal(SIGINT, SIG_DFL);
                signal(SIGQUIT, SIG_DFL);
                if(chk_pipe(cmdvector, arg_cnt)){
                    
                  //  printf("has pipe\n"); 
                    execute_pipe(cmdvector);
                    exit(0);
                }
              //  else printf("no pipe\n"); 
                redirection_read(cmdvector, &arg_cnt);
                redirection_write(cmdvector, &arg_cnt);
           
                // exit
                if(strcmp(cmdvector[0], "exit")==0){
                    exit(-1);
                }
                // cd
                else if(strcmp(cmdvector[0], "cd")==0){
                    // send SIGUSR1 to parent for chaning directory
                    kill(getppid(), 10);
                    exit(0);
                }   
                // background
                else if(cmdvector[arg_cnt - 1] != NULL && strcmp(cmdvector[arg_cnt - 1],"&")==0){
                    signal(SIGINT, SIG_IGN);
                    signal(SIGQUIT, SIG_IGN);
                    for(int i=0;i<arg_cnt-1;i++){
                        if(cmdvector[i+1] != NULL)
                            temp[i] = cmdvector[i];
                    } 
                    
                    pid = fork();
                    if(pid == 0){
                        execvp(temp[0], temp);
                        fatal("main()");
                    }
                
                    else if(pid > 0)
                        exit(0);

                    else
                        fatal("main()");    
                }
                else  {
                    execvp(cmdvector[0], cmdvector);
                    fatal("main()");
                }
          case -1:
              fatal("main()");
          default: 
              signal(SIGCHLD, sig_chld);
              wait(&status);
                  if(WIFEXITED(status)){
                      if(WEXITSTATUS(status) == 255)
                          exit(0);
                  }
        }
  }
  return 0;
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        