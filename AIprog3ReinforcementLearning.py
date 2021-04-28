from collections import defaultdict
from collections import OrderedDict
import random

class MarkovDecisionProcess:
    #n              number of non-terminating nodes: int
    #t              number of reward nodes: int
    #rounds         Sequence rounds: int
    #frequency      Print after this many rounds -- if 0 print only at end: int
    #M              speed hyperparameter: larger M takes longer to change from choosing random action to choosing best action: int
    #markov         Markov Decision Process (MDP) layout: defaultdict(list)
    #rewards        rewards at terminating nodes: defaultdict(int)
    #Count          (state, action) pair visits: OrderedDict
    #Total          (state, action) pair rewards: OrderedDict

    def __init__(self, filename, outputFile):
        self.readFile(filename)
        self.Count = OrderedDict()
        self.Total = OrderedDict()
        for (S,A) in self.markov.keys():
            self.Count[(S,A)] = 0
            self.Total[(S,A)] = 0

        # Rounds of Sequences
        for i in range(self.rounds):
            start = random.choice(range(self.n))
            seq = self.sequence(start)
            endState = seq.pop()
            for pair in set(seq):
                self.Count[pair] += 1
                self.Total[pair] += self.rewards[endState]
            if self.frequency > 0 and (i + 1) % self.frequency == 0:
                self.printOutput(i+1, outputFile)

        self.printOutput(self.rounds, outputFile)

    # Reads in file and initializes self variables
    def readFile(self, filename):
        self.markov = defaultdict(list)
        self.rewards = defaultdict(int)
        with open(filename, 'r') as reader:
            # Line 1 -- Number of non-terminating nodes, Number of terminating nodes, Rounds, Print frequency, speed hyperparameter
            self.n, self.t, self.rounds, self.frequency, self.M = [int(string) for string in reader.readline().split(" ")]

            # Line 2 -- Rewards
            line2 = reader.readline().split(" ")
            for i in range(0, len(line2) - 1, 2):
                self.rewards[int(line2[i])] = int(line2[i+1])

            # Remaining Lines -- MDP structure
            for x in reader:
                xArray = x.split(' ')
                state, action = [int(string) for string in xArray[0].split(':')]
                for i in range(1,len(xArray)-1, 2):
                    self.markov[(state, action)].append((int(xArray[i]), float(xArray[i+1])))

    # Develops each sequence round
    def sequence(self, state):
        action = self.chooseAction(state)
        endStates = [s[0] for s in self.markov[(state, action)]]
        probabilities = [p[1] for p in self.markov[(state, action)]]
        newState = random.choices(endStates, weights = probabilities, k=1)[0]
        if (newState not in list(self.rewards.keys())):
            return [(state, action)] + self.sequence(newState)  # haven't reached an end-state, further the sequence
        else:
            return [(state, action)] + [newState] # reward node reached, may return

    # Algorithm to choose action in sequence
    def chooseAction(self, s):
        pairs = [pair for pair in self.markov.keys() if pair[0] == s]   # all state, action pairs for input state
        actions = [x[1] for x in pairs]     # all potential actions for input state

        avg = OrderedDict()
        zeroCount = []
        for state,action in pairs:
            if self.Count[(state,action)] == 0:
                zeroCount.append(action)        # list of unvisited states
            else:
                avg[action] = self.Total[state,action] / self.Count[state,action]   # average reward for previously visited states
        if len(zeroCount) > 0:
            return random.choice(zeroCount)     # choose random unvisited state

        bottom = min(self.rewards.values())     # min reward
        top = max(self.rewards.values())        # highest reward
        c = 0
        savg = OrderedDict()                    # scaled average reward in range [0.25, 1]
        for state,action in pairs:
            savg[action] = 0.25 + 0.75*(avg[action] - bottom) / (top - bottom)  
            c += self.Count[state, action]

        norm = 0
        up = OrderedDict()
        for action in actions:
            up[action] = savg[action]**(c/self.M)   # unnormalized probability
            norm += up[action]

        p = OrderedDict()
        for action in actions:
            p[action] = up[action] / norm           # normalized probability

        probabilities = [weight[1] for weight in list(p.items())]
        choice = random.choices(actions, weights = probabilities, k=1)
        return choice[0]       

    # Prints Output
    def printOutput(self, round, filename):
        with open(filename, "a") as file:
            pairs = [pair for pair in self.Count.keys()]

            file.write("After " + str(round) + " rounds\n")

            # Counts
            file.write("Count:\n")
            sPrev = 0
            for S,A in pairs:
                if S == sPrev:
                    file.write("["  + str(S) + "," + str(A) + "]=" + str(self.Count[S,A]) + ". ")
                else:
                    file.write("\n[" + str(S) + "," + str(A) + "]=" + str(self.Count[S,A]) + ". ")
                sPrev = S
            file.write("\n")

            # Rewards
            file.write("\nTotal:")
            sPrev = 0
            for S,A in pairs:
                if S == sPrev:
                    file.write("["   + str(S) + "," + str(A) + "]=" + str(self.Total[S,A]) + ". ")
                else:
                    file.write("\n[" + str(S) + "," + str(A) + "]=" + str(self.Total[S,A]) + ". ")
                sPrev = S
            file.write("\n")

            # Calculate Best Action for each node
            BestAction = {}
            sPrev = 0
            bestRatio = -1
            for S,A in pairs:
                if self.Count[S,A] == 0:
                    ratio = -1
                else:
                    ratio = self.Total[S,A] / self.Count[S,A]

                if (S != sPrev) or (ratio > bestRatio):
                    bestRatio = ratio
                    BestAction[S] = A

                sPrev = S
            
            # Print Best Actions
            file.write("\nBest action: ")
            for S,A in BestAction.items():
                if A != -1:
                    file.write(str(S) + ":" + str(A) + ". ")
                else:
                    file.write(str(S) + ":U. ")
            file.write("\n\n")


# Initial (Main) commands
MarkovDecisionProcess("Prog3input2.txt", "Prog3output2.txt")