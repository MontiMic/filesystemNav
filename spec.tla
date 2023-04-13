-------------------------------- MODULE spec --------------------------------
EXTENDS TLC, Integers, Sequences

(*--fair algorithm CLI
    variables
        buckets = <<0,0,0>>;
        queue = <<4, 12, 24, 6, 15, 9, 3, 26, 32, 11, 17, 2>>;
        queueMutex = 0;
        bucketsMutex = 0;
    define
    AllDone == pc[1] = "Done" /\ pc[2] = "Done"
    Correct == AllDone => buckets = <<5,4,3>>
    MutexQueue == []~(pc[1] = "RemoveHead" /\ pc[2] = "RemoveHead")
    MutexBucket == []~(pc[1] = "IncrementBucket" /\ pc[2] = "IncrementBucket")
    end define;

    fair+ process thread \in {1,2}
    variables
        h = 0;
        bucketIdx = 0;
        tmp = 0;
    begin
    GetQueueMutex:
        await queueMutex = 0;
        queueMutex := 1;
    CheckQueueEmpty:
        if queue = <<>> then
            queueMutex := 0;
            goto Done;
        end if;
    GetHead:
        h := Head(queue);
    RemoveHead:
        queue := Tail(queue);
    ReleaseQueueMutex:
        queueMutex := 0;
    DecideBucket:
        if h < 10 then
            bucketIdx := 1;
        elsif h < 20 then
            bucketIdx := 2;
        else
            bucketIdx := 3;
        end if;
    GetBucketsMutex:
        await bucketsMutex = 0;
        bucketsMutex := 1;
    GetBucketValue:
        tmp := buckets[bucketIdx];
    IncrementBucket:
        buckets[bucketIdx] := tmp + 1;
    ReleaseBucketsMutex:
        bucketsMutex := 0;
    Loop:
        goto GetQueueMutex;
    end process;


end algorithm;*)
\* BEGIN TRANSLATION (chksum(pcal) = "7c28162a" /\ chksum(tla) = "33008de4")
VARIABLES buckets, queue, queueMutex, bucketsMutex, pc

(* define statement *)
AllDone == pc[1] = "Done" /\ pc[2] = "Done"
Correct == AllDone => buckets = <<5,4,3>>
MutexQueue == []~(pc[1] = "RemoveHead" /\ pc[2] = "RemoveHead")
MutexBucket == []~(pc[1] = "IncrementBucket" /\ pc[2] = "IncrementBucket")

VARIABLES h, bucketIdx, tmp

vars == << buckets, queue, queueMutex, bucketsMutex, pc, h, bucketIdx, tmp >>

ProcSet == ({1,2})

Init == (* Global variables *)
        /\ buckets = <<0,0,0>>
        /\ queue = <<4, 12, 24, 6, 15, 9, 3, 26, 32, 11, 17, 2>>
        /\ queueMutex = 0
        /\ bucketsMutex = 0
        (* Process thread *)
        /\ h = [self \in {1,2} |-> 0]
        /\ bucketIdx = [self \in {1,2} |-> 0]
        /\ tmp = [self \in {1,2} |-> 0]
        /\ pc = [self \in ProcSet |-> "GetQueueMutex"]

GetQueueMutex(self) == /\ pc[self] = "GetQueueMutex"
                       /\ queueMutex = 0
                       /\ queueMutex' = 1
                       /\ pc' = [pc EXCEPT ![self] = "CheckQueueEmpty"]
                       /\ UNCHANGED << buckets, queue, bucketsMutex, h, 
                                       bucketIdx, tmp >>

CheckQueueEmpty(self) == /\ pc[self] = "CheckQueueEmpty"
                         /\ IF queue = <<>>
                               THEN /\ queueMutex' = 0
                                    /\ pc' = [pc EXCEPT ![self] = "Done"]
                               ELSE /\ pc' = [pc EXCEPT ![self] = "GetHead"]
                                    /\ UNCHANGED queueMutex
                         /\ UNCHANGED << buckets, queue, bucketsMutex, h, 
                                         bucketIdx, tmp >>

GetHead(self) == /\ pc[self] = "GetHead"
                 /\ h' = [h EXCEPT ![self] = Head(queue)]
                 /\ pc' = [pc EXCEPT ![self] = "RemoveHead"]
                 /\ UNCHANGED << buckets, queue, queueMutex, bucketsMutex, 
                                 bucketIdx, tmp >>

RemoveHead(self) == /\ pc[self] = "RemoveHead"
                    /\ queue' = Tail(queue)
                    /\ pc' = [pc EXCEPT ![self] = "ReleaseQueueMutex"]
                    /\ UNCHANGED << buckets, queueMutex, bucketsMutex, h, 
                                    bucketIdx, tmp >>

ReleaseQueueMutex(self) == /\ pc[self] = "ReleaseQueueMutex"
                           /\ queueMutex' = 0
                           /\ pc' = [pc EXCEPT ![self] = "DecideBucket"]
                           /\ UNCHANGED << buckets, queue, bucketsMutex, h, 
                                           bucketIdx, tmp >>

DecideBucket(self) == /\ pc[self] = "DecideBucket"
                      /\ IF h[self] < 10
                            THEN /\ bucketIdx' = [bucketIdx EXCEPT ![self] = 1]
                            ELSE /\ IF h[self] < 20
                                       THEN /\ bucketIdx' = [bucketIdx EXCEPT ![self] = 2]
                                       ELSE /\ bucketIdx' = [bucketIdx EXCEPT ![self] = 3]
                      /\ pc' = [pc EXCEPT ![self] = "GetBucketsMutex"]
                      /\ UNCHANGED << buckets, queue, queueMutex, bucketsMutex, 
                                      h, tmp >>

GetBucketsMutex(self) == /\ pc[self] = "GetBucketsMutex"
                         /\ bucketsMutex = 0
                         /\ bucketsMutex' = 1
                         /\ pc' = [pc EXCEPT ![self] = "GetBucketValue"]
                         /\ UNCHANGED << buckets, queue, queueMutex, h, 
                                         bucketIdx, tmp >>

GetBucketValue(self) == /\ pc[self] = "GetBucketValue"
                        /\ tmp' = [tmp EXCEPT ![self] = buckets[bucketIdx[self]]]
                        /\ pc' = [pc EXCEPT ![self] = "IncrementBucket"]
                        /\ UNCHANGED << buckets, queue, queueMutex, 
                                        bucketsMutex, h, bucketIdx >>

IncrementBucket(self) == /\ pc[self] = "IncrementBucket"
                         /\ buckets' = [buckets EXCEPT ![bucketIdx[self]] = tmp[self] + 1]
                         /\ pc' = [pc EXCEPT ![self] = "ReleaseBucketsMutex"]
                         /\ UNCHANGED << queue, queueMutex, bucketsMutex, h, 
                                         bucketIdx, tmp >>

ReleaseBucketsMutex(self) == /\ pc[self] = "ReleaseBucketsMutex"
                             /\ bucketsMutex' = 0
                             /\ pc' = [pc EXCEPT ![self] = "Loop"]
                             /\ UNCHANGED << buckets, queue, queueMutex, h, 
                                             bucketIdx, tmp >>

Loop(self) == /\ pc[self] = "Loop"
              /\ pc' = [pc EXCEPT ![self] = "GetQueueMutex"]
              /\ UNCHANGED << buckets, queue, queueMutex, bucketsMutex, h, 
                              bucketIdx, tmp >>

thread(self) == GetQueueMutex(self) \/ CheckQueueEmpty(self)
                   \/ GetHead(self) \/ RemoveHead(self)
                   \/ ReleaseQueueMutex(self) \/ DecideBucket(self)
                   \/ GetBucketsMutex(self) \/ GetBucketValue(self)
                   \/ IncrementBucket(self) \/ ReleaseBucketsMutex(self)
                   \/ Loop(self)

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == (\E self \in {1,2}: thread(self))
           \/ Terminating

Spec == /\ Init /\ [][Next]_vars
        /\ WF_vars(Next)
        /\ \A self \in {1,2} : SF_vars(thread(self))

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION 
====

=============================================================================
\* Modification History
\* Last modified Wed Apr 12 19:53:37 CEST 2023 by Sen
\* Created Wed Apr 12 19:12:28 CEST 2023 by Sen
