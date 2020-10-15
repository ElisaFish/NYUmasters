function TF = TotalForce(PA,PB,QB)
    TF = ForceMatrix(PA,PB) * QB;
end