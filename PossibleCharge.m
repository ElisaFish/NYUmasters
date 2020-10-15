function C = PossibleCharge(PA,PB,TF)
    C = ForceMatrix(PA,PB) \ TF;
end